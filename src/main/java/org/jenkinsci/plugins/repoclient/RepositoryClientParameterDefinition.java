package org.jenkinsci.plugins.repoclient;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;
import org.apache.commons.lang.StringUtils;
import net.sf.json.JSONObject;
import hudson.Extension;
import hudson.model.SimpleParameterDefinition;
import hudson.model.ParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.model.ParameterValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class RepositoryChoiceParameterDefinition extends
		SimpleParameterDefinition {

	private final List<String> choices;
	private final String defaultValue;

	@DataBoundConstructor
	public RepositoryChoiceParameterDefinition(String name, String choices,
			String description) {
		super(name, description);
		System.out.println("Constructor1 called");
		(new Exception()).printStackTrace();
		this.choices = Arrays.asList(choices.split("\\r?\\n"));
		if (choices.length() == 0) {
			throw new IllegalArgumentException("No choices found");
		}
		defaultValue = null;
	}

	public RepositoryChoiceParameterDefinition(String name, String[] choices,
			String description) {
		super(name, description);
		System.out.println("Constructor2 called");
		this.choices = new ArrayList<String>(Arrays.asList(choices));
		if (this.choices.isEmpty()) {
			throw new IllegalArgumentException("No choices found");
		}
		defaultValue = null;
	}

	private RepositoryChoiceParameterDefinition(String name, List<String> choices,
			String defaultValue, String description) {
		super(name, description);
		System.out.println("Constructor3 called");
		this.choices = choices;
		this.defaultValue = defaultValue;
	}

	@Override
	public ParameterDefinition copyWithDefaultValue(ParameterValue defaultValue) {
		System.out.println("copyWithDefaultValue");
		if (defaultValue instanceof StringParameterValue) {
			StringParameterValue value = (StringParameterValue) defaultValue;
			return new RepositoryChoiceParameterDefinition(getName(), choices,
					value.value, getDescription());
		} else {
			return this;
		}
	}

	@Exported
	public List<String> getChoices() {
		System.out.println("Choices: " + choices);
		(new Exception()).printStackTrace();
		return choices;
	}

	public String getChoicesText() {
		System.out.println("Choices Text: " + choices);
		return StringUtils.join(choices, "\n");
	}

	@Override
	public StringParameterValue getDefaultParameterValue() {
		System.out.println("getDefaultParameterValue");
		return new StringParameterValue(getName(),
				defaultValue == null ? choices.get(0) : defaultValue,
				getDescription());
	}

	private StringParameterValue checkValue(StringParameterValue value) {
		if (!choices.contains(value.value))
			throw new IllegalArgumentException("Illegal choice: " + value.value);
		return value;
	}

	@Override
	public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
		System.out.println("createValue: JSONObject: " + jo);
		StringParameterValue value = req.bindJSON(StringParameterValue.class,
				jo);
		value.setDescription(getDescription());
		return checkValue(value);
	}

	public StringParameterValue createValue(String value) {
		System.out.println("createValue: value: " + value);
		return checkValue(new StringParameterValue(getName(), value,
				getDescription()));
	}

	@Extension
	public static class DescriptorImpl extends ParameterDescriptor {
		@Override
		public String getDisplayName() {
			System.out.println("getDisplayName");
			return "Maven Repository Artifact - Version List";//Messages.ChoiceParameterDefinition_DisplayName();
		}

		@Override
		public String getHelpFile() {
			System.out.println("getHelpFile");
			return "/choice.html";
		}
	}

}