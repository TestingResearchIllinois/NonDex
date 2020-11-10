import xml.etree.ElementTree as ET
import re
import xml.dom.minidom as minidom
import os
import sys

def nondex_xml_element():
    plugin = ET.Element('plugin')
    
    groupId = ET.SubElement(plugin, 'groupId')
    groupId.text = 'edu.illinois'
    
    artifactId = ET.SubElement(plugin, 'artifactId')
    artifactId.text = 'nondex-maven-plugin'
    
    version = ET.SubElement(plugin, 'version')
    version.text = '1.1.2'

    return plugin

def idflakies_element():
    plugin = ET.Element('plugin')
    
    plugin_groupId = ET.SubElement(plugin, 'groupId')
    plugin_groupId.text = 'edu.illinois.cs'
    
    plugin_artifactId = ET.SubElement(plugin, 'artifactId')
    plugin_artifactId.text = 'testrunner-maven-plugin'
    
    plugin_version = ET.SubElement(plugin, 'version')
    plugin_version.text = '1.0'
    
    dependencies = ET.SubElement(plugin, 'dependencies')
    dependency = ET.SubElement(dependencies, 'dependency')
    dep_groupId = ET.SubElement(dependency, 'groupId')
    dep_groupId.text = 'edu.illinois.cs'
    
    dep_artifactId = ET.SubElement(dependency, 'artifactId')
    dep_artifactId.text = 'idflakies'
    
    dep_version = ET.SubElement(dependency, 'version')
    dep_version.text = '1.0.2'
    
    config = ET.SubElement(plugin, 'configuration')
    class_name = ET.SubElement(config, 'className')
    class_name.text = 'edu.illinois.cs.dt.tools.detection.DetectorPlugin'
    return plugin

def get_namespace(element):
    m = re.match('\{.*\}', element.tag)
    return m.group(0) if m else ''

def find_elem(target, branch, ns):
    for child in branch:
        if child.tag == '{}{}'.format(ns, target):
            return child
    return None

def add_plugin_dependency(to_be_added, root, ns):
    build = find_elem('build', root, ns)
    if not build:
        build = ET.Element('{}build'.format(ns))
        root.append(build)

    plugins = find_elem('plugins', build, ns)
    if not plugins:
        plugins = ET.Element('{}plugins'.format(ns))
        build.append(plugins)
    
    plugins.append(to_be_added)
    
def xml_prettify(root, indent='  '):
    stringified = ET.tostring(root).decode()
    e = minidom.parseString(stringified)
    dom_string = e.toprettyxml(indent=indent)
    dom_string = os.linesep.join([s for s in dom_string.splitlines() if s.strip()])
    return dom_string

def modify_pom(path_to_pom):
    xml_tree = ET.parse(path_to_pom)
    root = xml_tree.getroot()
    namespace = get_namespace(root)
    if namespace != '':
        ET.register_namespace('', get_namespace(root)[1:-1])

    add_plugin_dependency(nondex_xml_element(), root, namespace)
    add_plugin_dependency(idflakies_element(), root, namespace)

    dom_str = xml_prettify(root, indent='  ')

    with open(path_to_pom, 'w') as f:
        f.write(dom_str)

def modify_all_poms(path):
    potential_path = '{}/pom.xml'.format(path)
    try:
        modify_pom(potential_path)
    except FileNotFoundError:
        pass
    
    for dir in os.listdir(path):
        next_dir = os.path.join(path, dir)
        if os.path.isdir(next_dir):
            modify_all_poms(next_dir)

if __name__ == "__main__":
    if len(sys.argv) == 1:
        print('Missing argument: python3 pom-modify.py <path-to-maven-project>')
    else:
        if os.path.exists(sys.argv[1]):
            modify_all_poms(sys.argv[1])
        else:
            print("No path found")
