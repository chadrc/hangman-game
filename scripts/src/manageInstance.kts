#!/usr/bin/env kscript

@file:CompilerOpts("-jvm-target 1.8")
@file:DependsOn("software.amazon.awssdk:aws-sdk-java:2.2.0")

import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest

val ec2 = Ec2Client.create()!!

val describeInstancesResponse = ec2.describeInstances()!!

val instance = describeInstancesResponse.reservations().flatMap { it.instances() }

val onlyId = instance.first().instanceId()!!

ec2.terminateInstances(TerminateInstancesRequest.builder().apply {
    instanceIds(onlyId)
}.build())

println("terminated $onlyId")

