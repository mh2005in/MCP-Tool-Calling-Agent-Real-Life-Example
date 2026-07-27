# JSON-configured Java MCP server

This project exposes HTTP APIs as Model Context Protocol (MCP) tools. Tool
definitions live in a JSON file, so new API mappings can be added without
changing Java code.

The server uses the official MCP Java SDK and communicates over standard
input/output (`stdio`). Standard output is reserved for MCP messages; runtime
diagnostics are written to standard error.

## Requirements

- JDK 24
- Maven 3.9 or newer, or the included Maven Wrapper

## Build

On Windows:

```powershell
.\mvnw.cmd clean verify
```

On macOS or Linux:

```bash
./mvnw clean verify
```

The executable jar is created at:

```text
target/json-api-mcp-server-0.1.0-SNAPSHOT.jar
```

## Run

**Prerequisites:** the backend must be running on `http://localhost:8080`
before starting the MCP server.

Start the backend first (from the `backend/` directory):

```powershell
# Windows
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24.0.2"
& "C:\Program Files\apache-maven-3.9.16\bin\mvn.cmd" spring-boot:run
```

Then start the MCP server using `mcprun.cmd` (from the `MCPServer/` directory).
It sets `JAVA_HOME`, `MAVEN_HOME`, and `PATH` automatically:

```powershell
.\mcprun.cmd
```

Tool definitions are bundled at `src/main/resources/config/tools.json`.

Override with an external tools file:

```powershell
.\mcprun.cmd -Dexec.args="C:\config\tools.json"
```

## MCP client configuration

Example client configuration for Claude Code (`.claude/settings.json`):

```json
{
  "mcpServers": {
    "immigration-api": {
      "command": "C:/Users/mh200/Projects/Immigration-Consultation/MCPServer/mcprun.cmd",
      "args": [],
      "env": {
        "MCP_API_KEY": "replace-with-your-mcp-api-key"
      }
    }
  }
}
```

## Available tools

| Tool | Description |
|------|-------------|
| `list_clients` | List clients for a consultant |
| `get_client` | Get a single client by ID |
| `create_client` | Create a new client |
| `summarize_intake` | Get masked intake responses grouped by section |
| `classify_document` | Classify a document by filename (rule-based + LLM fallback) |
| `detect_missing_documents` | Get checklist status to identify implicitly missing docs |
| `check_inconsistencies` | Get consistency report (expired, missing, duplicates) |
| `draft_reminder_email` | Get pending reminders for email draft generation |
| `extract_timeline` | Get combined travel/work/relationship timeline |
| `summarize_translation` | Validate and summarize user-provided translated text |
| `get_case_overview` | Get holistic case summary (intake + docs + checklist) |
| `validate_output` | AI guardrail — sanitize text before presenting to user |

## LLM provider configuration

This MCP server does not embed an LLM — it is designed to be called by an LLM
client (Claude via Claude Code, Claude Desktop, or any MCP-compatible client).
The LLM provides reasoning, summarization, and analysis; the tools provide
data access and guardrails.

**LLM configuration** is managed on the client side:
- **Claude Code**: uses the model configured in your Claude Code session
- **Claude Desktop**: uses the model selected in the desktop app
- **Custom MCP clients**: configure the Anthropic API key and model via your
  client's settings (e.g., `ANTHROPIC_API_KEY` env var, model ID like
  `claude-sonnet-4-6`)

Prompt templates are embedded in tool descriptions — each tool's `description`
field tells the LLM how to use the returned data and when to call
`validate_output`.

## Mapping format

Tool definitions live in `src/main/resources/config/tools.json`. Each entry
contains the MCP tool metadata and an HTTP request definition:

```json
{
  "tools": [
    {
      "name": "get_client",
      "description": "Get a client by numeric ID",
      "inputSchema": {
        "type": "object",
        "properties": {
          "clientId": {
            "type": "integer"
          }
        },
        "required": ["clientId"],
        "additionalProperties": false
      },
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/clients/{{clientId}}",
        "headers": {
          "Authorization": "Bearer {{env:BACKEND_TOKEN}}"
        },
        "query": {},
        "timeoutSeconds": 30
      }
    }
  ]
}
```

Template values:

- `{{argumentName}}` reads a tool argument.
- `{{user.address.city}}` reads a nested argument.
- `{{env:VARIABLE_NAME}}` reads an environment variable.
- An exact placeholder in a JSON request body preserves the argument's JSON
  type. Embedded placeholders are converted to text.

Supported HTTP methods are `GET`, `POST`, `PUT`, `PATCH`, and `DELETE`.
Configured URLs must use `http` or `https`.

## Notes

- Secrets should be supplied through environment placeholders, not committed
  in `tools.json`.
- A non-2xx API response is returned to the MCP client as a tool error with
  the HTTP status and response body.
- URLs are fixed by configuration. Tool arguments only fill placeholders and
  query values.
