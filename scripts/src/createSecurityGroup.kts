#!/usr/bin/env kscript

@file:CompilerOpts("-jvm-target 1.8")
@file:DependsOn("software.amazon.awssdk:aws-sdk-java:2.2.0")

import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressRequest
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupRequest
import software.amazon.awssdk.services.ec2.model.IpPermission
import software.amazon.awssdk.services.ec2.model.IpRange

val ec2 = Ec2Client.create()!!

val createSecurityGroupResponse = ec2.createSecurityGroup(
    CreateSecurityGroupRequest.builder()
        .description("Test Group")
        .groupName("TestGroup")
        .build()
)!!

val updateSecurityGroupResponse =
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
