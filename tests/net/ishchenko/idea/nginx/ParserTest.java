package net.ishchenko.idea.nginx;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageExtension;
import com.intellij.testFramework.ParsingTestCase;
import net.ishchenko.idea.nginx.parser.NginxParserDefinition;

public class ParserTest extends ParsingTestCase {
    // Needs to be called to add to Language.ourRegisteredClasses.
    // Otherwise we get an error "nginx doesn't participate in view provider",
    // which is caused by the view provider language being "ANY".
    private static Language l = NginxLanguage.INSTANCE;

    public ParserTest() {
        super("", "conf", new NginxParserDefinition());
    }

    public void testParsingData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }

    @Override
    protected <T> void addExplicitExtension(LanguageExtension<T> instance, Language language, T object) {
        super.addExplicitExtension(instance, language, object);
    }
}
