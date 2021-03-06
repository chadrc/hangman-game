#!/usr/bin/env kscript

@file:CompilerOpts("-jvm-target 1.8")
@file:DependsOn("software.amazon.awssdk:aws-sdk-java:2.2.0")

import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.*

val securityGroupName = "TestGroup"
val keyPairName = "TestKeyPair"

val ec2 = Ec2Client.create()!!

val createSecurityGroupResponse = ec2.createSecurityGroup(
    CreateSecurityGroupRequest.builder()
        .description(securityGroupName)
        .groupName(securityGroupName)
        .build()
)!!

ec2.authorizeSecurityGroupIngress(
    AuthorizeSecurityGroupIngressRequest.builder().apply {
        groupId(createSecurityGroupResponse.groupId())

        ipPermissions(
            IpPermission.builder().apply {
                ipProtocol("tcp")
                fromPort(22)
                toPort(22)
                ipRanges(
                    IpRange.builder().apply {
                        cidrIp("0.0.0.0/0")
                    }.build()
                )
            }.build(),

            IpPermission.builder().apply {
                ipProtocol("tcp")
                fromPort(3000)
                toPort(3000)
                ipRanges(
                    IpRange.builder().apply {
                        cidrIp("0.0.0.0/0")
                    }.build()
                )
            }.build()
        )
    }.build()
)!!

println("Created security group ${createSecurityGroupResponse.groupId()}")

val createKeyPairResponse = ec2.createKeyPair(
    CreateKeyPairRequest.builder().apply {
        keyName(keyPairName)
    }.build()
)!!

println("name: ${createKeyPairResponse.keyName()}")
println("fingerprint: ${createKeyPairResponse.keyFingerprint()}")
println("material: ${createKeyPairResponse.keyMaterial()}")

val createInstanceResponse = ec2.runInstances(RunInstancesRequest.builder().apply {
    imageId("ami-009d6802948d06e52")
    instanceType(InstanceType.T3_MICRO)
    keyName(keyPairName)
    maxCount(1)
    minCount(1)
    securityGroups(securityGroupName)
}.build())!!

println("Created instance ${createInstanceResponse.instances().first().instanceId()}")