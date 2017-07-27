File nondexDirectory = new File( basedir, ".nondex" );

sleep(3000)
assert nondexDirectory.isDirectory();

// Every run adds 5 directories. Latest and jar are static
assert (nondexDirectory.list().length - 2) % 5 == 0;
