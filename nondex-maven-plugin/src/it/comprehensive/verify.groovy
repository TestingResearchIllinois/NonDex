File nondexDirectory = new File( basedir, ".nondex" );

sleep(2000)

assert nondexDirectory.isDirectory();

assert nondexDirectory.list().length == 7
