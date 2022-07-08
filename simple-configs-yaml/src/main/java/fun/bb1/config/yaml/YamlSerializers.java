package fun.bb1.config.yaml;

import fun.bb1.config.serializer.InlineSerializer;
import fun.bb1.yaml.IYamlElement;
import fun.bb1.yaml.YamlArray;
import fun.bb1.yaml.YamlObject;
import fun.bb1.yaml.YamlPrimitive;

public final class YamlSerializers {

	public static final void registerSerializers() {
		final YamlAdapter adapter = new YamlAdapter();
		adapter.register();
		new InlineSerializer<IYamlElement>((p)->adapter.translate(p), (t)->adapter.translate(t)).register(IYamlElement.class);
		new InlineSerializer<YamlPrimitive>((p)->adapter.translate(p).getAsYamlPrimitive(), (t)->adapter.translate(t)).register(YamlPrimitive.class);
		new InlineSerializer<YamlObject>((p)->adapter.translate(p).getAsYamlObject(), (t)->adapter.translate(t)).register(YamlObject.class);
		new InlineSerializer<YamlArray>((p)->adapter.translate(p).getAsYamlArray(), (t)->adapter.translate(t)).register(YamlArray.class);
	}
	
	private YamlSerializers() { }
	
}
