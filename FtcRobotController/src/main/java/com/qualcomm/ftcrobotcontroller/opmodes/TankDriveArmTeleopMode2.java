package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by smh30 on 12/8/2015. Version 3.3
 */
public class TankDriveArmTeleopMode2 extends OpMode {

    DcMotor leftDrive;
    DcMotor rightDrive;
    DcMotor shoulder;
    DcMotor elbow;
    Servo allClearFinger;
    Servo leftZipLineFlipper;
    Servo rightZipLineFlipper;

    ServoController servoC1;

    double leftY = 1/3;
    double rightY = 1/3;

    double shoulderPower = 1/4;
    double elbowPower = 1/2;
    double fingerPosition;
    double leftZipLineFlipperPosition;
    double rightZipLineFlipperPosition;

    ElapsedTime time;
    double count;

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

        //Pressing the A or B buttons lock and unlock the shoulder and the elbow.
        //This is useful in the end game.
        if (this.gamepad2.b) {
            lockedElbow = false;
            lockedShoulder = false;
        }
        if (this.gamepad2.a) {
            lockedShoulder = true;
            lockedElbow = true;
        }

        //Normal driving is the value of the gamepad's joystick's y-position divided by 3.
        //Speedy driving is the 3 times the "normal" driving value
        //Update 6/1/2016: Left bumper must be on and left stick y must not equal zero
        //Hopefully this addresses our speedy driving lag bug.
        if (this.gamepad1.left_bumper && this.gamepad1.left_stick_y != 0) {
            leftY = Range.clip(leftY * 3, -1, 1);
        } else {
            leftY = Range.clip(this.gamepad1.left_stick_y / 3, -1, 1);
        }

        if (this.gamepad1.right_bumper && this.gamepad1.right_stick_y != 0) {
            rightY = Range.clip(rightY * 3, -1, 1);
        } else {
            rightY = Range.clip(this.gamepad1.right_stick_y / 3, -1, 1);
        }

        lockShoulder();
        lockElbow();
        controlLeftZipLineFlipper();
        controlRightZipLineFlipper();
        controlAllClearSignalFinger();

        setUpArm();

        //softwareArmStop();

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
        telemetry.addData("shoulderPosition", shoulder.getCurrentPosition());
        telemetry.addData("elbow: ", elbowPower);
        telemetry.addData("allClearFinger", fingerPosition);
        telemetry.addData("leftZipLineFlipper", leftZipLineFlipperPosition);
        telemetry.addData("rightZipLineFlipper", rightZipLineFlipperPosition);
    }

    private void softwareArmStop() {
        if (this.gamepad2.left_stick_y > 0 && this.shoulder.getCurrentPosition() <= 0) {
            shoulderPower = 0;
        }
    }

    private void setUpArm() {
        if (this.gamepad2.y) {

            if (count == 1) {
                time = new ElapsedTime();
            }

            count = time.time();
            this.shoulder.setTargetPosition(1050);


            if (this.shoulder.getCurrentPosition() <= this.shoulder.getTargetPosition()) {
                shoulderPower = -0.2/Math.pow(1.1, count);
            }
            if (this.shoulder.getCurrentPosition() >= this.shoulder.getTargetPosition()) {
                shoulderPower = 0.2/Math.pow(1.1, count);
            }
        } else {
            count = 1;
        }
    }

    private void lockElbow() {
        if (lockedElbow) {
            elbowPower = 0.1;
        } else if (this.gamepad2.right_bumper) {
            elbowPower = Range.clip(elbowPower * 2, -1, 1);
        } else {
            elbowPower = Range.clip(this.gamepad2.right_stick_y / 2, -1, 1);
        }
    }

    private void lockShoulder() {
        if (lockedShoulder) {
            shoulderPower = 0.4;
        } else if (this.gamepad2.left_bumper) {
            shoulderPower = Range.clip(shoulderPower * 5, -1, 1);
        } else if (this.gamepad2.left_stick_y > 0 ){
            shoulderPower = Range.clip(this.gamepad2.left_stick_y / 5, -1, 1);
        } else {
            shoulderPower = Range.clip(this.gamepad2.left_stick_y / 5, -1, 1);
        }
    }

    private void controlAllClearSignalFinger() {
        //Control the all clear signal finger
        if (this.gamepad2.dpad_down) {
            fingerPosition = Range.clip(fingerPosition + 0.01, 0, 1);
        }
        if (this.gamepad2.dpad_up) {
            fingerPosition = Range.clip(fingerPosition - 0.01, 0, 1);
        }

    }

    private void controlRightZipLineFlipper() {
        //Control the right zip line flipper
        if (this.gamepad1.y) {
            rightZipLineFlipperPosition = Range.clip(rightZipLineFlipperPosition + 0.01, 0, 1);
        }
        if (this.gamepad1.a) {
            rightZipLineFlipperPosition = Range.clip(rightZipLineFlipperPosition - 0.01, 0, 1);
        }
    }

    private void controlLeftZipLineFlipper() {
        //Control the left zip line flipper
        if (this.gamepad1.dpad_down) {
            leftZipLineFlipperPosition = Range.clip(leftZipLineFlipperPosition + 0.01, 0, 1);
        }
        if (this.gamepad1.dpad_up) {
            leftZipLineFlipperPosition = Range.clip(leftZipLineFlipperPosition - 0.01, 0, 1);
        }
    }


    private void PowerSetter() throws Exception {
        this.rightDrive.setPower(rightY);
        this.leftDrive.setPower(-leftY);
        this.shoulder.setPower(-shoulderPower);
        this.elbow.setPower(-elbowPower);
        this.allClearFinger.setPosition(fingerPosition);
        this.leftZipLineFlipper.setPosition(leftZipLineFlipperPosition);
        this.rightZipLineFlipper.setPosition(rightZipLineFlipperPosition);
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
        this.allClearFinger = hardwareMap.servo.get("allClearFinger");
        this.leftZipLineFlipper = hardwareMap.servo.get("leftZipLineFlipper");
        this.rightZipLineFlipper = hardwareMap.servo.get("rightZipLineFlipper");

        this.servoC1 = hardwareMap.servoController.get("servoC1");
        servoC1.pwmEnable();

        //Set the allClearFinger power
        fingerPosition = 0;
        leftZipLineFlipperPosition = 1;
        rightZipLineFlipperPosition = 1;

        count = 1;

        // I essentially copied this from SynchTeleOp
        // Configure the knobs of the hardware according to how you've wired your
        // robot. Here, we assume that there are no encoders connected to the motors,
        // so we inform the motor objects of that fact.
        this.leftDrive.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.rightDrive.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.shoulder.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.elbow.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

}
