# TowerAnalyzer - Memory / Task Tracker

## Outstanding Tasks

### Persistence Layer
- [x] Implement persistence layer for **bots**
- [x] Implement persistence layer for **guardians**
- [x] Implement persistence layer for **lab slot planning**
- [x] Implement persistence layer for **tournament battle condition tracker**
- [x] Update Workshop with cost data for regular workshop upgrades

### Database
- [x] Create a way to **back up the SQLite database**
- [x] Add a **cache layer** for the database to speed up retrieval

### Version History
- [x] Implement a process where **lab, workshop, workshop+, Module, Guardian, Bot and Ultimate Weapons updates** are tracked across the website and used to automatically create a new version history entry
  - Hold the data in such a way an AI Agent can access it
  - Allow the AI agent to help assess what kind version update it should be (patch, minor, major)
  - Allow the AI agent to discuss the assessment with the user
  - Allow the AI agent to invoke the createVersion api after discussion with user
- [X] Implement a process where the current lab starting value tracks current value of the lab so it tracks the current lab state

### MCP Server
- [x] Update the MCP server to use **all data from the database**
- [x] Allow the AI agent to pass in **flags to only include relevant sections** of data
- [x] Add /api/lab-slots to the MCP Server.  Need to figure out how to keep it lean

### Skills
- [x] Module Skill
- [x] Ultimate Weapon Skill
- [ ] Create a Lab skill - there is a decided gap on what particular labs do.  
  - thinks Chain Thunder helps with kill, but it is a damage reduction
  - thinks CF reduction % helps with slows, but it is a damage reduction
- [ ] 

---

*Last updated: 2026-06-07*
