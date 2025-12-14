File nondexDirectory = new File( basedir, ".nondex" );

sleep(3000)
assert nondexDirectory.isDirectory();

// 3 execution adds 12 directories (1 clean / 3 shuffled). 3 other overhead files
assert (nondexDirectory.list().length - 3) == 12;
