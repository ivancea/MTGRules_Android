package com.ivancea.MTGRules.appfunctions

import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunctionDeclaration
import androidx.appfunctions.AppFunctionException
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import androidx.appfunctions.AppFunctionAppUnknownException
import com.ivancea.MTGRules.model.Rule
import com.ivancea.MTGRules.services.RulesService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@RequiresApi(36)
@AndroidEntryPoint
@AppFunctionServiceEntryPoint(
    serviceName = "RulesAppFunctionService",
    appFunctionXmlFileName = "rules_app_function_service",
)
abstract class BaseRulesAppFunctionService : AppFunctionService() {
    @Inject internal lateinit var rulesService: RulesService

    /**
     * Lists all bundled editions of the Magic Comprehensive Rules in date order.
     * @return Stable edition IDs and effective dates; the last edition is the default.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun listRulesets(): List<RulesetInfo> = execute {
        rulesService.rulesSources.map { RulesetInfo(it.date.toString(), it.date) }
    }

    /**
     * Lists a page of immediate children in document order, without descriptions or descendants.
     * Leaves have no children. Lettered provisions are siblings of their numbered provision.
     *
     * Illustrative tree (specific numbered provisions vary by edition):
     * ```text
     * Root
     * ├── 1.
     * │   └── 100.
     * │       ├── 100.1.
     * │       └── 100.1a
     * └── Glossary
     *     └── Creature
     * ```
     *
     * @param rulesetId Edition ID (YYYY-MM-DD). Omitted means latest bundled, independent of UI selection.
     * @param parentRuleId Parent ID or glossary term. Omitted means root. Numeric terminal dots are optional.
     * @param offset Zero-based number of immediate children to skip, not a rule number. Defaults to zero.
     * @param limit Positive page size. Omitted means 50. Offsets beyond the end return an empty page.
     * @return Child IDs and headings, total child count, resolved edition, and next offset or null.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun listRules(
        rulesetId: String? = null,
        parentRuleId: String? = null,
        offset: Int = 0,
        limit: Int? = null,
    ): RulesPage = withRules(rulesetId) { edition, rules ->
        RulesQueries.list(edition, rules, parentRuleId, offset, limit ?: 50)
    }

    /**
     * Retrieves one rule's ID, heading and full own description, including examples but not descendants.
     * @param rulesetId Edition ID (YYYY-MM-DD). Omitted means latest bundled, independent of UI selection.
     * @param id Rule ID or exact glossary term. Numeric terminal dots are optional.
     * @return One node's content and the resolved edition ID.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun getRule(rulesetId: String? = null, id: String): RuleResult =
        withRules(rulesetId) { edition, rules -> RulesQueries.get(edition, rules, id) }

    /**
     * Searches the entire selected edition, including section headings, numbered provisions and glossary
     * entries, using the same matching logic as the app's search bar. Matches each node's own ID and text,
     * not its descendants' text. Results are paginated in document order, without relevance ranking.
     * @param searchTerm Case-insensitive substring query, not whole-word matching. Unquoted text splits on
     * whitespace; all terms and phrases must match the same node (AND, not OR). Each may match either its
     * ID/glossary term or its text. Double quotes preserve one contiguous phrase: combat damage matches
     * both words anywhere in the node, while "combat damage" requires that exact contiguous phrase.
     * Mixed queries such as '"combat damage" step' require both the phrase and the additional term.
     * Backslash-escaped double quotes are literal characters, not phrase delimiters. An unmatched opening
     * quote treats the remaining text as a phrase. Empty or whitespace-only input matches every node.
     * @param rulesetId Edition ID (YYYY-MM-DD). Omitted means latest bundled, independent of UI selection.
     * @param offset Zero-based number of matching nodes to skip, not a rule number. Defaults to zero.
     * @param limit Positive page size. Omitted means 50. Offsets beyond the end return an empty page.
     * @return A page of matching nodes with their own content, total match count, resolved edition ID,
     * and next offset or null when finished.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun searchRules(
        searchTerm: String,
        rulesetId: String? = null,
        offset: Int = 0,
        limit: Int? = null,
    ): RulesSearchResult = withRules(rulesetId) { edition, rules ->
        RulesQueries.search(edition, rules, searchTerm, offset, limit ?: 50)
    }

    private suspend fun <T> withRules(id: String?, action: (String, List<Rule>) -> T): T = execute {
        val source = RulesQueries.resolveSource(rulesService.rulesSources, id)
        action(source.date.toString(), rulesService.loadRules(source))
    }

    private suspend fun <T> execute(action: () -> T): T = withContext(Dispatchers.IO) {
        try {
            action()
        } catch (e: CancellationException) {
            throw e
        } catch (e: AppFunctionException) {
            throw e
        } catch (e: IllegalArgumentException) {
            throw AppFunctionInvalidArgumentException(e.message ?: "Invalid argument.")
        } catch (e: NoSuchElementException) {
            throw AppFunctionElementNotFoundException(e.message ?: "Rule or edition not found.")
        } catch (e: Exception) {
            throw AppFunctionAppUnknownException("Unable to read bundled rules.")
        }
    }
}
