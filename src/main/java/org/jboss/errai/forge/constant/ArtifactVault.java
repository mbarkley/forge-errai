package org.jboss.errai.forge.constant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.forge.facet.base.AbstractBaseFacet;
import org.jboss.forge.project.dependencies.Dependency;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
public final class ArtifactVault {

  public static final String ERRAI_GROUP_ID = "org.jboss.errai";

  /**
   * An enumeration of Maven dependency artifacts used by various facets.
   * 
   * @author Max Barkley <mbarkley@redhat.com>
   */
  public static enum DependencyArtifact {
    // Non-errai
    GwtUser("gwt-user", "com.google.gwt"),
    Guava("guava", "com.google.guava"),
    GuavaGwt("guava-gwt", "com.google.guava"),
    Hsq("hsqldb", "hsqldb"),
    JUnit("junit", "junit"),
    GwtSlf4j("gwt-slf4j", "de.benediktmeurer.gwt-slf4j"),
    JavaxInject("javax.inject", "javax.inject"),
    CdiApi("cdi-api", "javax.enterprise"),
    JsrApi("jsr250-api", "javax.annotation"),
    JavaxValidation("validation-api", "javax.validation"),
    HibernateAnnotations("hibernate-commons-annotations", "org.hibernate.common"),
    HibernateJpa("hibernate-jpa-2.0-api", "org.hibernate.javax.persistence"),
    HibernateCore("hibernate-core", "org.hibernate"),
    HibernateEntityManager("hibernate-entitymanager", "org.hibernate"),
    HibernateValidator("hibernate-validator", "org.hibernate"),
    JbossLogging("jboss-logging", "org.jboss.logging"),
    JaxrsApi("jaxrs-api", "org.jboss.resteasy"),
    JbossInterceptors("jboss-interceptors-api_1.1_spec", "org.jboss.spec.javax.interceptor"),
    JbossTransaction("jboss-transaction-api_1.1_spec", "org.jboss.spec.javax.transaction"),
    WeldServletCore("weld-servlet-core", "org.jboss.weld.servlet"),
    WeldCore("weld-core", "org.jboss.weld"),
    WeldApi("weld-api", "org.jboss.weld"),
    WeldSpi("weld-spi", "org.jboss.weld"),
    XmlApis("xml-apis", "xml-apis"),
    JettyNaming("jetty-naming", "org.mortbay.jetty"),

    // plugins
    Clean("maven-clean-plugin", "org.apache.maven.plugins"),
    Dependency("maven-dependency-plugin", "org.apache.maven.plugins"),
    Compiler("maven-compiler-plugin", "org.apache.maven.plugins"),
    GwtPlugin("gwt-maven-plugin", "org.codehaus.mojo"),
    War("maven-war-plugin", "org.apache.maven.plugins"),
    JbossPlugin("jboss-as-maven-plugin", "org.jboss.as.plugins"),

    // errai
    ErraiNetty("netty", "org.jboss.errai.io.netty"),
    ErraiJboss("errai-cdi-jboss"),
    JbossSupport("errai-jboss-as-support"),
    ErraiCommon("errai-common"),
    ErraiTools("errai-tools"),
    ErraiBus("errai-bus"),
    ErraiCdiClient("errai-cdi-client"),
    ErraiWeldIntegration("errai-weld-integration"),
    ErraiCdiJetty("errai-cdi-jetty"),
    ErraiCodegenGwt("errai-codegen-gwt"),
    ErraiIoc("errai-ioc"),
    ErraiDataBinding("errai-data-binding"),
    ErraiJavaxEnterprise("errai-javax-enterprise"),
    ErraiJaxrsClient("errai-jaxrs-client"),
    ErraiJpaClient("errai-jpa-client"),
    ErraiNavigation("errai-navigation"),
    ErraiUi("errai-ui");

    private final String artifactId;
    private final String groupId;

    private DependencyArtifact(final String artifactId, final String groupId) {
      this.artifactId = artifactId;
      this.groupId = groupId;
    }

    private DependencyArtifact(final String id) {
      this(id, ERRAI_GROUP_ID);
    }

    /**
     * @return The artifact id of this dependency.
     */
    public String getArtifactId() {
      return artifactId;
    }

    /**
     * @return The group id of this dependency.
     */
    public String getGroupId() {
      return groupId;
    }

    /**
     * Returns the string {@code groupId} + ":" + {@code artifactId}.
     */
    public String toString() {
      return String.format("%s:%s", groupId, artifactId);
    }

    private static Map<String, DependencyArtifact> artifacts = new HashMap<String, ArtifactVault.DependencyArtifact>();

    static {
      for (final DependencyArtifact artifact : DependencyArtifact.values()) {
        artifacts.put(artifact.getGroupId() + ":" + artifact.getArtifactId(), artifact);
      }
    }

    /**
     * Lookup a {@link DependencyArtifact} by the unique combination of it's
     * group id and artifact id.
     */
    public static DependencyArtifact valueOf(String groupId, String artifactId) {
      return artifacts.get(groupId + ":" + artifactId);
    }
  }

  /**
   * Blacklist of Maven dependencies which cannot be deployed in various
   * profiles.
   */
  private static final Map<String, Set<String>> blacklist = new HashMap<String, Set<String>>();

  static {
    // Wildfly/Jboss blacklist
    blacklist.put(AbstractBaseFacet.MAIN_PROFILE, new HashSet<String>());
    final Set<String> mainProfileBlacklist = blacklist.get(AbstractBaseFacet.MAIN_PROFILE);
    mainProfileBlacklist.add(DependencyArtifact.ErraiTools.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiJboss.toString());
    mainProfileBlacklist.add(DependencyArtifact.Hsq.toString());
    mainProfileBlacklist.add(DependencyArtifact.JUnit.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiNetty.toString());
    mainProfileBlacklist.add(DependencyArtifact.GwtSlf4j.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiCodegenGwt.toString());
    mainProfileBlacklist.add(DependencyArtifact.JavaxInject.toString());
    mainProfileBlacklist.add(DependencyArtifact.CdiApi.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiCdiJetty.toString());
    mainProfileBlacklist.add(DependencyArtifact.GuavaGwt.toString());
    mainProfileBlacklist.add(DependencyArtifact.JsrApi.toString());
    mainProfileBlacklist.add(DependencyArtifact.JavaxValidation.toString());
    mainProfileBlacklist.add(DependencyArtifact.HibernateAnnotations.toString());
    mainProfileBlacklist.add(DependencyArtifact.HibernateJpa.toString());
    mainProfileBlacklist.add(DependencyArtifact.HibernateCore.toString());
    mainProfileBlacklist.add(DependencyArtifact.HibernateEntityManager.toString());
    mainProfileBlacklist.add(DependencyArtifact.HibernateValidator.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiDataBinding.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiJavaxEnterprise.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiJaxrsClient.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiJpaClient.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiNavigation.toString());
    mainProfileBlacklist.add(DependencyArtifact.ErraiUi.toString());
    mainProfileBlacklist.add(DependencyArtifact.JbossLogging.toString());
    mainProfileBlacklist.add(DependencyArtifact.JaxrsApi.toString());
    mainProfileBlacklist.add(DependencyArtifact.JbossInterceptors.toString());
    mainProfileBlacklist.add(DependencyArtifact.JbossTransaction.toString());
    mainProfileBlacklist.add(DependencyArtifact.WeldServletCore.toString());
    mainProfileBlacklist.add(DependencyArtifact.WeldCore.toString());
    mainProfileBlacklist.add(DependencyArtifact.WeldApi.toString());
    mainProfileBlacklist.add(DependencyArtifact.WeldSpi.toString());
    mainProfileBlacklist.add(DependencyArtifact.XmlApis.toString());
    mainProfileBlacklist.add(DependencyArtifact.JettyNaming.toString());
  }

  public static boolean isBlacklisted(final String identifier) {
    for (final String profile : blacklist.keySet()) {
      if (blacklist.get(profile).contains(identifier))
        return true;
    }

    return false;
  }

  public static boolean isBlacklisted(final Dependency dep) {
    return isBlacklisted(dep.getGroupId() + ":" + dep.getArtifactId());
  }

  public static String getBlacklistedProfile(final String identifier) {
    for (final String profile : blacklist.keySet()) {
      if (blacklist.get(profile).contains(identifier))
        return profile;
    }

    return null;
  }

  public static String getBlacklistedProfile(final Dependency dep) {
    return getBlacklistedProfile(dep.getGroupId() + ":" + dep.getArtifactId());
  }

  public static Collection<String> getBlacklistProfiles() {
    return blacklist.keySet();
  }

  public static Collection<String> getBlacklistedArtifacts(final String profileId) {
    final Set<String> artifacts = blacklist.get(profileId);

    return (artifacts != null ? artifacts : new ArrayList<String>(0));
  }

}
