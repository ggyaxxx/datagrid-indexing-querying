package com.example.demo;

import org.infinispan.protostream.annotations.Proto;

@Proto
public record Customer(String id, String name) {}