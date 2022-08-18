Assuming you want to work on the feature **analytics** and you will be working on a branch `ftr.analytics`

# Before you start working on a feature
1. Make sure your changelist is empty
2. Fetch the repository
3. Carefully delete unwanted local branches
4. Create feature branch off the `origin/dev` branch
    1. VCS > Git > Branches..
    2. Select `origin/dev` > New branch from selected..
    3. Name your local branch e.g. `ftr.analytics`
    4. Keep **Checkout Branch** checkbox checked
5. Make sure that the `ftr.analytics` is currently checked-out branch
5. Now, you can start working on the feature.

# After completing the work

1. Thoroughly test the feature and make sure that everything is working as expected!
2. Commit your changes (in currently checked-out branch i.e. `ftr.analytics`)
3. Fetch the repository
4. Checkout `origin/dev` branch
    1. VCS > Git > Branches..
    2. Select `origin/dev` > Checkout
5. Merge `ftr.analytics` into `dev` branch
    1. VCS > Git > Branches..
    2. `ftr.analytics` > Merge into Current
    3. If you are hit with merge conflicts, and not sure about how to resolve, **contact your manager**
6. Publish your changes 
    1. VCS > Git > Branches..
    2. `dev` > Push
7. Carefully delete unwanted local `dev` and `ftr.analytics` branches
