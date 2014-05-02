package org.cad.interruptus.features;

import cucumber.api.CucumberOptions;
import org.junit.runner.RunWith;

import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(format = { "json:target/report.json" })
public class RunCucumberIT 
{

}