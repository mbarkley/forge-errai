package org.jboss.errai.forge.util;

import javax.inject.Inject;

import org.jboss.errai.forge.facet.aggregate.AggregatorFacetReflections;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;

public class FeatureCompleter extends SimpleTokenCompleter {
  
  @Inject
  private AggregatorFacetReflections aggregatorReflections;
  
  @Override
  public Iterable<?> getCompletionTokens() {
    return aggregatorReflections.iterable();
  }

}
