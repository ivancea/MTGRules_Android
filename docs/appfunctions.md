# Local AppFunctions integration

MTG Rules exposes four read-only AppFunctions on supported Android devices:

| Function | Parameters | Response |
| --- | --- | --- |
| `listRulesets` | None | Bundled edition IDs (`YYYY-MM-DD`) and effective dates |
| `listRules` | `rulesetId?`, `parentRuleId?`, `offset = 0`, `limit = 50` | Immediate child IDs/headings, `total`, `nextOffset`, resolved `rulesetId` |
| `getRule` | `rulesetId?`, `id` | One node's ID, title, description, resolved `rulesetId` |
| `searchRules` | `searchTerm`, `rulesetId?`, `offset = 0`, `limit = 50` | A page of matching nodes' own content, `total`, `nextOffset`, resolved `rulesetId` |

Omit `rulesetId` unless the user explicitly requests a different edition. The
default is the latest bundled edition, independent of UI selection, not a live
online update. All data comes from the existing rules registry and parser.

`listRules` omits descriptions and descendants. Offsets are zero-based child
indices in document order, not rule numbers. An omitted parent means root; a
leaf has no children. An offset beyond the end returns an empty page.
`nextOffset = null` means the page is final. Negative offsets and non-positive
limits are invalid. At the AppFunctions boundary, an omitted nullable `limit`
is resolved to 50 in the implementation (rather than relying on a Kotlin
default being passed by the framework).

IDs returned by the tools are canonical, including punctuation. Numeric IDs
also accept an omitted terminal dot. Glossary terms are exact IDs under
`Glossary`. Existing tree structure is preserved: `101.1a`-style provisions
are siblings of `101.1.`, not children. Section headings and glossary terms
populate `title`; numbered provisions without headings have empty titles.
`description` is the node's own existing text, including examples, never its
descendants. No titles or summaries are generated from rule prose.

Search reuses `RulesSearchUtils`: case-insensitive substring AND matching across
each node's ID/text, double-quoted contiguous phrases, escaped literal quotes,
document order, no ranking. Blank queries match all nodes. Search uses the same
pagination defaults and validation as `listRules`, but offsets count matching
nodes rather than immediate children. `total` counts all matches; follow
`nextOffset` to retrieve subsequent pages, keeping the query and edition unchanged.

## Verification

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest assembleDebugAndroidTest
adb devices
adb shell cmd app_function help
```

Use an Android 16+ device/emulator with the `app_function` service. Consult its
actual help output for supported command options. If no service exists, local
AppFunctions transport testing is unavailable on that system image.

Install the debug APK on a development device using `adb install -r
app/build/outputs/apk/debug/app-debug.apk` (an existing production install signed
with a different key cannot be replaced with this debug build). Then inspect:

```powershell
adb shell cmd app_function list-app-functions --package com.ivancea.MTGRules
```

Copy the exact IDs from that output. Expected IDs use the declaring class:
`com.ivancea.MTGRules.appfunctions.BaseRulesAppFunctionService#listRulesets`,
`#listRules`, `#getRule`, and `#searchRules`.

For example, in a POSIX shell (preserve JSON quoting when adapting to PowerShell):

```sh
adb shell 'cmd app_function execute-app-function --package com.ivancea.MTGRules --function "com.ivancea.MTGRules.appfunctions.BaseRulesAppFunctionService#listRules" --parameters '\''{"offset":2,"limit":3}'\'''
```

Check omitted defaults, two consecutive glossary pages, a leaf, an unknown ID,
historical edition selection, and quoted search. Run instrumentation tests:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

`RulesAppFunctionsTest` checks generated service methods against real bundled
resources; ADB execution additionally verifies OS discovery, binding, and
serialization. The APK must include `assets/app_functions_schema.xsd` and
`assets/rules_app_function_service.xml`; its manifest registers the generated
service protected by `BIND_APP_FUNCTION_SERVICE` and app instructions metadata.

Gemini enrollment is not required for these local checks. Consumer Gemini
discovery/conversational invocation and production onboarding are separate,
deferred checks requiring access to Google's integration pipeline.
