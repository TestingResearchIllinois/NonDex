File nondexDirectory = new File( basedir, "module1/.nondex" );
assert nondexDirectory.exists();

nondexDirectory = new File( basedir, "module2/.nondex" );
assert nondexDirectory.exists();