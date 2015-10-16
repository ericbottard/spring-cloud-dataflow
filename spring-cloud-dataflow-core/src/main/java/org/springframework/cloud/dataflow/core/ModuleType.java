/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.dataflow.core;

/**
 * Enumeration of module types.
 *
 * @author Patrick Peralta
 */
public enum ModuleType {
	/**
	 * A module type that appears in a stream, at first position.
	 */
	source,

	/**
	 * A module type that appears in a stream, in middle position.
	 */
	processor,

	/**
	 * A module type that appears in a stream, in last position.
	 */
	sink,

	/**
	 * A module type to execute a short-lived process.
	 */
	task,

	/**
	 * A supporting library to a module, either as a single (jar) artifact, or as a bom.
	 */
	library
}
