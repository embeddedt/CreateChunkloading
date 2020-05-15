package com.grimmauld.createintegration;


import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_POWER = "power";
    public static final String CATEGORY_Belt_MACHINES = "belt_machines";
    public static final String SUBCATEGORY_DYNAMO = "dynamo";
    public static final String SUBCATEGORY_MOTOR = "motor";

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.IntValue ROLLER_SU;
    
    public static ForgeConfigSpec.IntValue DYNAMO_MAXPOWER;
    public static ForgeConfigSpec.IntValue DYNAMO_GENERATE_MULTIPLIER;
    public static ForgeConfigSpec.IntValue DYNAMO_SEND;
    public static ForgeConfigSpec.IntValue DYNAMO_SU;
    
    public static ForgeConfigSpec.IntValue MOTOR_CAPACITY;
    public static ForgeConfigSpec.IntValue MOTOR_MAXINPUT;
    public static ForgeConfigSpec.IntValue MOTOR_SPEED;
    public static ForgeConfigSpec.IntValue MOTOR_SU;
    public static ForgeConfigSpec.IntValue MOTOR_FE;


    static {

        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        COMMON_BUILDER.pop();
        
        COMMON_BUILDER.comment("Belt machines settings").push(CATEGORY_Belt_MACHINES);
        setupBeltMachinesConfig(COMMON_BUILDER, CLIENT_BUILDER);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Power settings").push(CATEGORY_POWER);

        setupDynamoConfig(COMMON_BUILDER, CLIENT_BUILDER);
        setupMotorConfig(COMMON_BUILDER, CLIENT_BUILDER);
        COMMON_BUILDER.pop();


        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
    
    private static void setupBeltMachinesConfig(ForgeConfigSpec.Builder COMMON_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
    	ROLLER_SU = COMMON_BUILDER.comment("Base SU for the Rolling Machine")
                .defineInRange("rollingMachineSU", 4, 0, Integer.MAX_VALUE);
    }    
    

    private static void setupDynamoConfig(ForgeConfigSpec.Builder COMMON_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        COMMON_BUILDER.comment("Dynamo settings").push(SUBCATEGORY_DYNAMO);

        DYNAMO_MAXPOWER = COMMON_BUILDER.comment("Maximum power buffer for the Stressed Out Dynamo generator")
                .defineInRange("maxPower", 100000, 0, Integer.MAX_VALUE);
        DYNAMO_GENERATE_MULTIPLIER = COMMON_BUILDER.comment("Power generation per rpm")
                .defineInRange("generate", 50, 0, Integer.MAX_VALUE);
        DYNAMO_SEND = COMMON_BUILDER.comment("Power transfer to send per tick")
                .defineInRange("send", 25000, 0, Integer.MAX_VALUE);
        DYNAMO_SU = COMMON_BUILDER.comment("Stress Units required to run the Dynamo")
                .defineInRange("su", 256, 0, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
    }
    
    private static void setupMotorConfig(ForgeConfigSpec.Builder COMMON_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        COMMON_BUILDER.comment("Motor settings").push(SUBCATEGORY_MOTOR);

        MOTOR_CAPACITY = COMMON_BUILDER.comment("Maximum power buffer for the Stressed Out Motor")
                .defineInRange("maxPower", 100000, 0, Integer.MAX_VALUE);
        MOTOR_MAXINPUT = COMMON_BUILDER.comment("Max FE/t input for the motor")
                .defineInRange("receive", 25000, 0, Integer.MAX_VALUE);
        MOTOR_SU = COMMON_BUILDER.comment("Generated base stress units")
                .defineInRange("su", 1024, 0, Integer.MAX_VALUE);
        MOTOR_SPEED = COMMON_BUILDER.comment("Motor Speed")
                .defineInRange("rpm", 16, 0, 256);
        MOTOR_FE = COMMON_BUILDER.comment("FE/t required to run the motor")
                .defineInRange("fe", 4096, 0, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
    }
    
    public static void loadConfig(ForgeConfigSpec spec, Path path) {
    	final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
    	configData.load();
    	spec.setConfig(configData);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {

    }
}