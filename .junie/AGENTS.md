# Rules the agent must follow before doing anything

- [ ] Read this file and `README.md` before acting.
- [ ] Look at the dependencies that the project uses and prioritize working with them.

# Feature development and decision-making

- Make small, targeted changes instead of building for hypothetical future needs.
- If something is unclear, ask before making assumptions.
- When possible always favor immutability.
- Follow Kotlin's [coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
- Always test the changes you make.
- The tests should be done in this order of priority:
  1. If it is business rules that do not depend on a framework: Use unit tests.
  2. If it is a framework-dependent tool: use the framework's own testing options (e.g., Test Slices in Spring Boot).
  3. Integration tests.

# Architecture guidelines

- Pay attention to the project existing structures and try to maintain the same pattern, aim for cohesion.
- Simple is better, do not create more files or abstractions than needed.

# Security and data handling

- Never log tokens or sensitive data.
- 
# Explicit prohibitions what agents must NOT do

- Do not bump versions of dependencies without asking first.
