File nondexDirectory = new File( basedir, ".nondex" );

assert nondexDirectory.isDirectory();

assert nondexDirectory.list().length == 7
