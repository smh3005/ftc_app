package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by smh30 on 12/8/2015. Version 3.2
 */
public class TankDriveArmTeleopMode2 extends OpMode {

    DcMotor leftDrive;
    DcMotor rightDrive;
    DcMotor shoulder;
    DcMotor elbow;

    double leftY = 1/3;
    double rightY = 1/3;

    double shoulderPower = 1/4;
    double elbowPower = 1/2;

    Boolean lockedElbow = false;
    Boolean lockedShoulder = false;


    @Override
    public void init() {
        try {
            initializeTankDriveArmTeleopMode2();
        } catch (Exception e) {
            DbgLog.error("Seth - Initialization error");
        }

    }

    @Override
    public void loop() {

        //Pressing the A or B buttons lock the shoulder and the elbow respectfully. This is useful in the end game.
        if (this.gamepad2.b) {
            lockedElbow = !lockedElbow;
        }
        if (this.gamepad2.a) {
            lockedShoulder = !lockedShoulder;
        }

        //Normal driving is the value of the gamepad's joystick's y-position divided by 3.
        //Speedy driving is the 3 times the normal driving value
        if (this.gamepad1.left_bumper) {
            leftY = Range.clip(leftY * 3, -1, 1);
        } else {
            leftY = Range.clip(this.gamepad1.left_stick_y / 3, -1, 1);
        }

        if (this.gamepad1.right_bumper) {
            rightY = Range.clip(rightY * 3, -1, 1);
        } else {
            rightY = Range.clip(this.gamepad1.right_stick_y / 3, -1, 1);
        }

        //Shoulder and elbow power
        //Same gearing strategy as the drive system
        if (lockedShoulder) {
            shoulderPower = 0.4;
        } else if (this.gamepad2.left_bumper) {
            shoulderPower = Range.clip(shoulderPower * 4, -1, 1);
        } else {
            shoulderPower = Range.clip(this.gamepad2.left_stick_y / 4, -1, 1);
        }

        if (lockedElbow) {
            elbowPower = 0.1;
        } else if (this.gamepad2.right_bumper) {
            elbowPower = Range.clip(elbowPower * 2, -1, 1);
        } else {
            elbowPower = Range.clip(this.gamepad2.right_stick_y / 2, -1, 1);
        }

        //set the power of the motors with the gamepad values
        try {
            PowerSetter();
        } catch (InterruptedException e) {
            DbgLog.error("Seth - Setting a motor power got an  interrupted exception error");
        } catch (RobotCoreException e) {
            DbgLog.error("Seth - Setting a motor power got an  robot core error");
        } catch (Exception e) {
            DbgLog.error("Seth - Setting a motor power got an regular exception error");
        }


        telemetry.addData("rightDrive: ", rightY);
        telemetry.addData("leftDrive: ", leftY);
        telemetry.addData("shoulder: ", shoulderPower);
        telemetry.addData("elbow: ", elbowPower);
    }


    private void PowerSetter() throws Exception {
        this.rightDrive.setPower(-rightY);
        this.leftDrive.setPower(leftY);
        this.shoulder.setPower(-shoulderPower);
        this.elbow.setPower(-elbowPower);
    }

    @Override
    public void stop() {
        this.rightDrive.setPower(0);
        this.leftDrive.setPower(0);
        this.shoulder.setPower(0);
        this.elbow.setPower(0);

    }

    private void initializeTankDriveArmTeleopMode2() {
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

}
