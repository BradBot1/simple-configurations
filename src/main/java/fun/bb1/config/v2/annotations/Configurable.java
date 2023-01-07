package fun.bb1.config.v2.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import fun.bb1.config.v2.IAnnotatedConfigurable;

/**
 * 
 * Copyright 2022 BradBot_1
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * A simple way to denote a field as configurable
 * 
 * @apiNote This is usually used with {@link IAnnotatedConfigurable} but can be omitted if you are using {@link fun.bb1.config.Configurable}
 * 
 * @author BradBot_1
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Configurable {
	/**
	 * @apiNote If "" is supplied the field name will be used, this may cause issues when dealing with obsufcation
	 * 
	 * @return The name to use in the key value pair
	 */
	public String value() default "";
	/**
	 * @apiNote If "" is supplied no comment will be shown
	 * 
	 * @return The comment to be displayed above the actual field
	 */
	public String comment() default "";
	/**
	 * @apiNote This value should never result in a key used elsewhere, elsewise conflicts may occur
	 * 
	 * @return The prefix to apply to {@link #value()} to generate the key name for the comment
	 * 
	 * @deprecated This is only here for backwards comparability, it is ignored by the config
	 */
	@Deprecated(since = "2.0.0")
	public String commentPrefix() default "comment-";
	
}
