**CADER: Complete Approach for RDF QuEry Relaxation**

This is the implementation of CADER, a new efficient and complete technique for determining optimal relaxations over RDF databases. Our method is based on  the strong  duality  between  MFSes (Minimal Failing Subsets) and XSSes (maXimal Succeeding Subsets).  This duality is shown to be the cornerstone of the efficiency of our approach. CADER identifies the root causes of failure and computes relaxations when the user query fails. We propose to evaluate the subqueries of the user query individually in a preprocessing step and base the subsequent computation of relaxations on these intermediate results.

In a nutshell, our method can be summarized in the following steps:
First, we calculate all minimal failing subsets of the user query before computing the entire set of relaxed queries.
Then, we compute the hitting sets of these MFSes. These hitting sets are in fact the complement of all the possible relaxations of the failing user query. for each minimal failing subset, we take its complement and  Accordingly, the complete set of relaxed queries is computed from the set of hitting sets in a direct way by taking the complement of each hitting set (i.e. all the triple patterns in the original query not in the hitting set).

---

## Data Initialization instructions
Our code is based on the same data initialization in [QaRS](https://forge.lias-lab.fr/projects/qars/wiki/Documentation).
1. Copy one of the LUBM data set files existing under resources.OWL into the specified directory of params[0] in QARSInitializationSample.class.
2. run **QARSInitializationSample** class.
3. data will be generated in the specified directory of params[3].
4. update **jenatdb.repository** in triplestores.config with the generated data directory

---

## Installing Additional Libraries

This project depends on several libraries from the [Boost project](https://www.boost.org/). To install the required dependencies on a Debian system, run

`sudo apt-get install libboost-program-options-dev libboost-log-develop`

It also uses the [moodycamel::ConcurrentQueue](https://github.com/cameron314/concurrentqueue) library, which is included here under the terms of the author's "Simplified BSD license".

---

## Clone a repository

Use these steps to clone from SourceTree, our client for using the repository command-line free. Cloning allows you to work on your files locally. If you don't yet have SourceTree, [download and install first](https://www.sourcetreeapp.com/). If you prefer to clone from the command line, see [Clone a repository](https://confluence.atlassian.com/x/4whODQ).

1. You’ll see the clone button under the **Source** heading. Click that button.
2. Now click **Check out in SourceTree**. You may need to create a SourceTree account or log in.
3. When you see the **Clone New** dialog in SourceTree, update the destination path and name if you’d like to and then click **Clone**.
4. Open the directory you just created to see your repository’s files.

Now that you're more familiar with your Bitbucket repository, go ahead and add a new file locally. You can [push your change back to Bitbucket with SourceTree](https://confluence.atlassian.com/x/iqyBMg), or you can [add, commit,](https://confluence.atlassian.com/x/8QhODQ) and [push from the command line](https://confluence.atlassian.com/x/NQ0zDQ).
