---
name: worktree
description: >-
  Create and clean up git worktrees for feature work per CLAUDE.md §13. Use when
  starting a new feature/branch in an isolated worktree, or when a PR has merged
  on GitHub and its worktree under .claude/worktrees/ needs removing. Covers the
  manual paths the .githooks/post-merge hook does NOT handle (creation, and
  cleanup when you never pull the merge into main locally).
---

# Git worktrees (§13)

Feature work lives in git worktrees under `.claude/worktrees/<name>/`, on a
branch named `mh/<kebab-name>` (§11). This skill covers the **interactive** parts
of §13. The `.githooks/post-merge` hook already auto-removes a worktree when you
`git pull` its merged branch **into `main`** locally — this skill is for the
paths the hook can't cover: **creating** a worktree, and **cleaning up** when the
PR merged on GitHub and you never pull into main.

Run all commands from the **main checkout** (the repo root), not from inside a
worktree.

## Create a worktree for new feature work

1. Pick a short kebab-case name for the work, e.g. `add-expiry-alerts`.
2. Create the worktree and its `mh/`-prefixed branch off `main` in one step:

   ```bash
   git worktree add .claude/worktrees/add-expiry-alerts -b mh/add-expiry-alerts main
   ```

   - Path per §13 (`.claude/worktrees/<name>/`), branch per §11 (`mh/<kebab>`).
   - If the branch already exists, drop `-b`: `git worktree add .claude/worktrees/<name> mh/<name>`.
3. Do the feature work inside `.claude/worktrees/<name>/`. Commit/push and open
   the PR as `mh2005in` (§11) from there.

## Clean up after a merge

Only remove a worktree **after the merge is verified**, and never one with
unmerged or uncommitted work.

1. **Confirm the PR actually merged** before removing anything:

   ```bash
   gh pr view <n> --json state,mergedAt
   ```

   Proceed only if `state` is `MERGED` (a non-null `mergedAt`).
2. **Remove the worktree, then prune the branch**, from the main checkout:

   ```bash
   git worktree remove .claude/worktrees/<name>
   git branch -d mh/<name>
   ```

   - Use plain `remove`/`-d` (no `--force`): git refuses to delete a dirty or
     unmerged tree, which is the safety net. If it refuses, **stop and report**
     — investigate, don't `--force` past it.
   - Add `--force` to `git worktree remove` **only** if the tree has intended
     leftover files you've confirmed are disposable.
3. If instead you `git pull` the merged branch into `main` locally, the
   `post-merge` hook does steps 2 automatically — you don't need this skill then.

## Safety rules (§13)

- **Never delete** the `main` checkout, the shared root `CLAUDE.md`, or a
  worktree with unmerged/uncommitted work.
- **Verify the merge first** — never remove a worktree on assumption.
- **Squash-merge caveat:** the hook and any ancestry check (`git merge-base
  --is-ancestor`) detect a merge by the branch tip being an ancestor of `main`.
  Under **squash merges** the branch commits never become ancestors, so
  ancestry-based cleanup won't recognize the merge — verify via `gh pr view`
  (step 1) and remove manually.
- Enable the hooks in a fresh clone (per-clone): `git config core.hooksPath .githooks`.
