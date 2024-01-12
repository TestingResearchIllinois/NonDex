import sys


def modify(path, version, subModules):
    f = open(path, "r")
    f2 = open(path, "r")
    inBuildScript = False
    inRepositories = False
    inDependency = False
    dependencySet = False
    repositorySet = False
    addedToSubproject = False
    result = ""
    cnt = 0
    inBuildScriptBraceCnt = 1

    dependencies = f"""
        classpath("edu.illinois:plugin:{version}")
"""

    full_buildscript = """buildscript {
    repositories {
      maven {
        url = uri('https://plugins.gradle.org/m2/')
      }
    }
    dependencies {
        classpath("edu.illinois:plugin:%s")
    }
}

""" % version

    mavenString = """
        maven {
            url = uri('https://plugins.gradle.org/m2/')
        }
"""


    lines = f2.read()
    lines = lines.replace(" ", "")
    if "buildscript{" not in lines:
        result += full_buildscript
        cnt = 3

    subprojectsInFile = False
    if "subprojects{" in lines:
        subprojectsInFile = True

    stack = []
    isInSubproject = False
    # add line to output by finding the location of repositories{}, buildscript{}
    for line in f.readlines():
        if cnt < 3:
            # add data
            if inDependency and not dependencySet:
                result += dependencies
                dependencySet = True
                cnt += 1

            if inRepositories and not repositorySet:
                result += mavenString
                repositorySet = True
                cnt += 1

            # update state variables
            if inBuildScript and "}" in line:
                inBuildScriptBraceCnt -= line.count("}")
            if inBuildScript and "{" in line:
                inBuildScriptBraceCnt += line.count("{")
            if inBuildScript and "repositories {" in line:
                inRepositories = True
                inDependency = False
            if "buildscript {" in line or "buildscript{" in line:
                inBuildScript = True
            if inBuildScript and "dependencies {" in line:
                inDependency = True
                inRepositories = False
            if inBuildScript and "}" in line and inBuildScriptBraceCnt == 0:
                if not dependencySet:
                    result += "\tdependencies {" + dependencies + "}\n"
                    dependencySet = True
                    cnt += 1
                if not repositorySet:
                    result += "\trepositories {" + mavenString + "}\n"
                    repositorySet = True
                    cnt += 1


        line_no_space = line.replace(" ", "")
        if subprojectsInFile and "subprojects{" in line_no_space.strip():
            isInSubproject = True
        if isInSubproject:
            for char in line_no_space.strip():
                if char == "{":
                    stack.append(char)
                elif char == "}" and stack[-1] == "{":
                    stack.pop()
                elif char == "}":
                    stack.append(char)
            if len(stack) == 0:
                result += "\tapply plugin: 'edu.illinois.nondex' \n"
                isInSubproject = False
                addedToSubproject = True

        if not isInSubproject and ("test {" in line or "test{" in line):
            result += "tasks.withType(Test) {\n"
            continue

        result += line

    # add lines at the end of the file
    result += "\napply plugin: 'edu.illinois.nondex'\n"

    if subModules > 0 and not addedToSubproject:
        result += """subprojects {
    apply plugin: 'edu.illinois.nondex'
}
        """

    output = open(path, "w")
    output.write(result)
    output.close()


if __name__ == "__main__":
    version = sys.argv[1]
    numberOfSubModules = sys.argv[2]

    for path in sys.stdin:
        path = path.strip()
        print("modify: " + path)
        modify(path, version, int(numberOfSubModules))

