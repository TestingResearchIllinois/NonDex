File nondexDirectory = new File(basedir, ".nondex");
sleep(3000);
assert nondexDirectory.isDirectory() : "NonDex directory should exist";

// Verify that nondex ran successfully with the settings property in argLine
// Should have 1 clean execution + 3 shuffled = 4 directories, plus 3 overhead files
assert (nondexDirectory.list().length - 3) == 4;

// If the settings property was removed the build would have failed before getting here