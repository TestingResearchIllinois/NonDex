File nondexDirectory = new File( basedir, ".nondex" );

assert nondexDirectory.isDirectory();

// Every run adds 5 directories. Latest and jar are static
assert (nondexDirectory.list().length - 2) % 5 == 0;

def nondexFolder = new File(".nondex")
dirs = nondexFolder.listFiles()

for (File f : dirs) {
    if (f.getName().startsWith("clean")) {
       failures = new File(f.getAbsolutePath() + File.pathSeparator + "failures");
       // in the clean phase all tests should fail, meaning at least 40
       assert Files.readAllLines(failures.getPath(), Charset.defaultCharset()).size() > 40;
    }
}
