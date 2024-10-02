package com.morningstar;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.apache.commons.lang3.ArrayUtils;

import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.morningstar", importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTest {
    static final String ROOT_PACKAGE = "com.morningstar..";
    static final String INFRA_PACKAGE = "com.morningstar.infra..";
    static final String SYSTEM_PACKAGE = "com.morningstar.system..";
    static final String BOOT_PACKAGE = "com.morningstar.boot..";
    static final String[] APPS_PACKAGE = {
            "com.morningstar.blog..",
            "com.morningstar.lab..",
            "com.morningstar.love..",
            "com.morningstar.pic..",
            "com.morningstar.proxy..",
    };


    /**
     * com.morningstar.* 只包含指定的包
     */
    @ArchTest
    @SuppressWarnings("unused")
    static final ArchRule only_allowed_packages_exist = classes()
            .that().resideInAPackage(ROOT_PACKAGE)
            .and().doNotHaveSimpleName("BackendApp")
            .should().resideInAnyPackage(
                    ArrayUtils.addAll(APPS_PACKAGE,
                            INFRA_PACKAGE,
                            SYSTEM_PACKAGE,
                            BOOT_PACKAGE
                    )
            );

    /**
     * infra 不依赖 boot、system、apps
     */
    @ArchTest
    @SuppressWarnings("unused")
    static final ArchRule infra_does_not_depend_on_anything_else = noClasses()
            .that().resideInAPackage(INFRA_PACKAGE)
            .should().dependOnClassesThat().resideInAnyPackage(
                    Stream.concat(
                            Stream.of(APPS_PACKAGE),
                            Stream.of(BOOT_PACKAGE, SYSTEM_PACKAGE)
                    ).toArray(String[]::new)
            );

    /**
     * system 不依赖 boot 或 apps
     */
    @ArchTest
    @SuppressWarnings("unused")
    static final ArchRule system_does_not_depend_on_boot_or_apps = noClasses()
            .that().resideInAPackage(SYSTEM_PACKAGE)
            .should().dependOnClassesThat().resideInAnyPackage(
                    Stream.concat(
                            Stream.of(APPS_PACKAGE),
                            Stream.of(BOOT_PACKAGE)
                    ).toArray(String[]::new)
            );

    /**
     * apps 不依赖 boot
     */
    @ArchTest
    @SuppressWarnings("unused")
    static final ArchRule apps_do_not_depend_on_boot = noClasses()
            .that().resideInAnyPackage(APPS_PACKAGE)
            .should().dependOnClassesThat().resideInAPackage(BOOT_PACKAGE);

    /**
     * web 不依赖 dao
     */
    @ArchTest
    @SuppressWarnings("unused")
    static final ArchRule web_should_not_depend_on_dao = noClasses()
            // 所有的 web 包下的类
            .that().resideInAPackage("com.morningstar..web..")
            // 都不能依赖任何 dao 包下的类
            .should().dependOnClassesThat().resideInAPackage("com.morningstar..dao..");
}
