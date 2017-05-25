package ru.colibri.template;

import io.appium.java_client.AppiumDriver;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import ru.alfabank.autotest.core.pages.IPage;
import ru.alfabank.autotest.core.pages.IPageProvider;
import ru.alfabank.autotest.core.settings.DriversSettings;
import ru.alfabank.autotest.core.settings.IJBConfigurator;
import ru.alfabank.autotest.core.settings.TestSettings;
import ru.alfabank.autotest.core.utils.FileUtils;
import ru.alfabank.autotest.settings.general.PagesLoader;

import java.io.File;
import java.util.List;

/**
 * Created by dolinskiyaleksandr on 07.11.16.
 */

public abstract class AbstractStories extends JUnitStories {

    protected Configuration configuration;
    @Autowired
    protected IJBConfigurator configurator;

    @Autowired
    private PagesLoader pagesLoader;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private TestSettings testSettings;

    protected void initPageProvider() {
        File pagesDirectory = fileUtils.getFileByPath(getDriversSettings().getPagesPath());
        List<IPage> pages = pagesLoader.loadPagesFromDirectory(pagesDirectory);
        getPageProvider().addPagesObject(pages);
    }

    @After
    public void after() {
        getDriver().quit();
    }

    @Override
    public Configuration configuration() {
        if (configuration == null) {
            configuration = configurator.createConfig();
            Embedder embedder = configuredEmbedder();
            embedder.useMetaFilters(testSettings.getFlagsMetaFilters());
            configurator.configure(embedder.embedderControls(), getDriversSettings());
        }
        return configuration;
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new SpringStepsFactory(configuration(), appContext);
    }

    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(CodeLocations.codeLocationFromPath(getDriversSettings().getStoryPath()),
                getDriversSettings().getStoryToInclude(), getDriversSettings().getStoryToExclude());
    }

    protected abstract DriversSettings getDriversSettings();

    protected abstract AppiumDriver getDriver();

    protected abstract IPageProvider getPageProvider();
}
