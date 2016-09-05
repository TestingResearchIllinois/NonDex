File nondexDirectory = new File( basedir, ".nondex" );

sleep(1000)

assert nondexDirectory.isDirectory();

assert nondexDirectory.list().length == 7
