package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by smh30 on 12/8/2015.
 */
public class TankDriveArmTeleopMode2 extends OpMode {

    DcMotor leftDrive;
    DcMotor rightDrive;
    DcMotor shoulder;
    DcMotor elbow;

    double leftY = 1/3;
    double rightY = 1/3;

    double shoulderPower;
    double elbowPower;


    @Override
    public void init() {

        //get references to the hardware from the hardware map
        this.leftDrive = hardwareMap.dcMotor.get("leftDrive");
        this.rightDrive = hardwareMap.dcMotor.get("rightDrive");

        this.shoulder = hardwareMap.dcMotor.get("shoulder");
        this.elbow = hardwareMap.dcMotor.get("elbow");

        // I essentially copied this from SynchTeleOp
        // Configure the knobs of the hardware according to how you've wired your
        // robot. Here, we assume that there are no encoders connected to the motors,
        // so we inform the motor objects of that fact.
        this.leftDrive.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.rightDrive.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.shoulder.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.elbow.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

    }

    @Override
    public void loop() {

        if (gamepad1.left_bumper) {
            try {
                leftY = Range.clip(leftY * 3, -1, 1);
            } catch (Exception e) {
                this.leftDrive.setPower(0);
                DbgLog.error("Seth - Left Bumper got an error");
            }
        } else {
            try {
                leftY = Range.clip(this.gamepad1.left_stick_y / 3, -1, 1);
            } catch (Exception e) {
                this.leftDrive.setPower(0);
                DbgLog.error("Seth - No Left Bumper got an error");
            }
        }
        if (gamepad1.right_bumper) {
            try {
                rightY = Range.clip(rightY * 3, -1, 1);
            } catch (Exception e) {
                this.rightDrive.setPower(0);
                DbgLog.error("Seth - Right Bumper got an error");
            }

        } else {
            try {
                rightY = Range.clip(this.gamepad1.right_stick_y / 3, -1, 1);
            } catch (Exception e) {
                this.rightDrive.setPower(0);
                DbgLog.error("Seth - No Right Bumper got an error");
            }

        }

        //set the power of the motors with the gamepad values
        try {
            this.leftDrive.setPower(leftY);
        } catch (Exception e) {
            this.leftDrive.setPower(0);
            DbgLog.error("Seth - Setting the leftDrive power got an error");
        }

        try {
            this.rightDrive.setPower(-rightY);
        } catch (Exception e) {
            this.rightDrive.setPower(0);
            DbgLog.error("Seth - Setting the rightDrive power got an error");
        }

        try {
            shoulderPower = (this.gamepad2.left_stick_y/4);
        } catch (Exception e){
            this.shoulder.setPower(0);
            DbgLog.error("Seth - Setting shoulderPower got an error");
        }

        try {
            elbowPower = (this.gamepad2.right_stick_y/2);
        } catch (Exception e) {
            this.elbow.setPower(0);
            DbgLog.error("Seth - Setting elbowPower got an error");
        }

        //Manipulate the arm;
        try {
            this.shoulder.setPower(-shoulderPower);
        } catch (Exception e) {
            this.shoulder.setPower(0);
            DbgLog.error("Seth - Setting shoulder's power to shoulderPower got an error");
        }

        try {
            this.elbow.setPower(-elbowPower);
        } catch (Exception e) {
            this.elbow.setPower(0);
            DbgLog.error("Seth - Setting elbow's power to elbowPower got an error");
        }

        try {
            telemetry.addData("rightDrive: ", rightY);
            telemetry.addData("leftDrive: ", leftY);
            telemetry.addData("shoulder: ", shoulderPower);
            telemetry.addData("elbow: ", elbowPower);
        } catch (Exception e) {
            DbgLog.error("Seth - Using telemetry.addData got an error");
        }


    }
    @Override
    public void stop() {
        this.rightDrive.setPower(0);
        this.leftDrive.setPower(0);
        this.shoulder.setPower(0);
        this.elbow.setPower(0);

    }

}
