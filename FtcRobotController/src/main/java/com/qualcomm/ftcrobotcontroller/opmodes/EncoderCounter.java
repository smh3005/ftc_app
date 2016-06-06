package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Created by smh30 on 6/6/2016.
 */
public class EncoderCounter extends OpMode{

    DcMotor leftDrive;
    DcMotor rightDrive;
    DcMotor shoulder;
    DcMotor elbow;

    @Override
    public void init() {
        try {
            initializeEncoderCounter();
        } catch (Exception e) {
            DbgLog.msg("Seth - Initializing Encoder Counter had an error");
        }
    }
    private void initializeEncoderCounter() {
        this.leftDrive = hardwareMap.dcMotor.get("leftDrive");
        this.rightDrive = hardwareMap.dcMotor.get("rightDrive");

        this.shoulder = hardwareMap.dcMotor.get("shoulder");
        this.elbow = hardwareMap.dcMotor.get("elbow");

        this.leftDrive.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.rightDrive.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.shoulder.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.elbow.setMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    @Override
    public void loop() {
        this.telemetry.addData("Right Count ", rightDrive.getCurrentPosition());
        this.telemetry.addData("Left Count ", leftDrive.getCurrentPosition());
        this.telemetry.addData("Right Count ", shoulder.getCurrentPosition());
        this.telemetry.addData("Right Count ", elbow.getCurrentPosition());

    }
}
