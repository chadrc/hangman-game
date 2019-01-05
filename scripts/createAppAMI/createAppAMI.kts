#!/usr/bin/env kscript

@file:CompilerOpts("-jvm-target 1.8")
@file:DependsOn("software.amazon.awssdk:aws-sdk-java:2.2.0")

import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.*
import software.amazon.awssdk.utils.builder.SdkBuilder
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

fun <B: SdkBuilder<B, T>, T> B.buildWith(block: B.() -> Unit): T {
    this.apply(block)
    return this.build()
}

val ec2 = Ec2Client.create()!!

val initFile = File("scripts/createAppAMI/init.sh").readBytes()
val encoded = Base64.getEncoder().encodeToString(initFile)!!

println("encoded file: $encoded")

val runInstancesResponse = ec2.runInstances(RunInstancesRequest.builder().buildWith {
    imageId("ami-009d6802948d06e52") // Amazon Linux 2 64-bit AMI (us-east-1)
    instanceType(InstanceType.T3_MICRO)
    maxCount(1)
    minCount(1)
    userData(encoded)

})!!

val onlyId = runInstancesResponse.instances().first().instanceId()!!

// wait for instance to be running
println("Waiting for instance ($onlyId) to be in running state")
var running = false

var iterCount = 1
val interval = 5L
while (!running) {
    TimeUnit.SECONDS.sleep(interval)

    println("Check Count: $iterCount (${iterCount * interval} seconds)")

    val describeInstanceResponse = ec2.describeInstances(DescribeInstancesRequest.builder().buildWith {
        instanceIds(onlyId)
    })

    val onlyInstance = describeInstanceResponse.reservations().flatMap { it.instances() }.first()

    if (onlyInstance.state().name().toString() == InstanceStateName.RUNNING.toString()) {
        running = true
    } else  {
        iterCount++
    }
}

val createImageResponse = ec2.createImage(CreateImageRequest.builder().buildWith {
    instanceId(onlyId)
    name("HangmanAppImage")
})!!

println("Created Image ${createImageResponse.imageId()}")

println("Terminating created instance")

ec2.terminateInstances(TerminateInstancesRequest.builder().buildWith {
    instanceIds(onlyId)
})

println("Terminated instance $onlyId")
