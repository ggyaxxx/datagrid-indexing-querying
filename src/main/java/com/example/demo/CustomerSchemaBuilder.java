package com.example.demo;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.ProtoSchema;

@ProtoSchema(schemaPackageName = "tutorial", includeClasses = Customer.class)
public interface CustomerSchemaBuilder extends GeneratedSchema {
}
