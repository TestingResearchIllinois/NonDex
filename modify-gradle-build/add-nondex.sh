cd $1
buildFile=$(./gradlew properties | grep buildFile | awk '{print $2}')
output=$(./gradlew projects)
echo "$output" | grep "No sub-projects" > /dev/null
sub=$?
echo ${sub}
grep "edu.illinois.nondex" ${buildFile}
if [ $? != 0 ]; then
        if [ "$sub" == 0 ]; then
                echo "\napply plugin: 'edu.illinois.nondex'" >> ${buildFile}
        else
        for p in ${projects}; do
                        subBuildFile=$(./gradlew :$p:properties | grep buildFile | awk '{print $2}')
                        sed -i 's/^\( \|\t\)*test /tasks.withType(Test) /' ${subBuildFile};
                done
        projects=$(./gradlew projects | grep Project | cut -f3 -d" " | tr -d "':")
                if [[ "$OSTYPE" == "darwin"* ]]; then
                        echo "\nsubprojects {\n    apply plugin: 'edu.illinois.nondex'\n}" >> ${buildFile}   # macOS
                else
                        echo -e "\nsubprojects {\n    apply plugin: 'edu.illinois.nondex'\n}" >> ${buildFile}   # Linux
                fi
        fi
    echo "buildscript {
        repositories {
            maven {
                url = uri('https://plugins.gradle.org/m2/')
            }
        }
        dependencies {
            classpath('edu.illinois:plugin:2.2.1')
        }
    }
    $(cat ${buildFile})" > ${buildFile}
fi
if [[ "$OSTYPE" == "darwin"* ]]; then
    sed -i '' 's/^\( \|\t\)*test /tasks.withType(Test) /' "${buildFile}"  # macOS
else
    sed -i 's/^\( \|\t\)*test /tasks.withType(Test) /' "${buildFile}"  # Linux
fi
