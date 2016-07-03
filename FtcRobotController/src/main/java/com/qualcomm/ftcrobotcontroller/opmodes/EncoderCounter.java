package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by smh30 on 6/6/2016.
 */
public class EncoderCounter extends OpMode{

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

    enum State {forward, backward, correct};
    State state;
    int shoulderTargetPosition;

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

        this.leftDrive.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.rightDrive.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        this.shoulder.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.elbow.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        //this.shoulder.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        //this.elbow.setMode(DcMotorController.RunMode.RESET_ENCODERS);

        state = state.forward;

    }

    @Override
    public void loop() {

        if (this.gamepad1.x) {
            this.rightDrive.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
            this.leftDrive.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        }

        if (this.gamepad1.y) {
            this.leftDrive.setMode(DcMotorController.RunMode.RESET_ENCODERS);
            this.rightDrive.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }

        if (this.gamepad2.x) {
            this.shoulder.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        }

        if (this.gamepad2.y) {
            switch (state) {
                case forward:
                    shoulderTargetPosition = 1245;
                    shoulder.setTargetPosition(shoulderTargetPosition);
                    shoulder.setPower(0.4);
                    if (shoulder.getCurrentPosition() > shoulderTargetPosition) {
                        state = state.backward;
                    }

                    if (shoulder.getCurrentPosition() == shoulderTargetPosition) {
                        break;
                    }
                    break;
                case backward:
                    shoulder.setPower(-0.25);
                    shoulder.setTargetPosition(shoulderTargetPosition);
                    if (shoulder.getCurrentPosition() < shoulderTargetPosition) {
                        state = state.forward;
                    }

                    if (shoulder.getCurrentPosition() == shoulderTargetPosition) {
                        break;
                    }
                    break;
            }
        }

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

        this.telemetry.addData("Right Count ", rightDrive.getCurrentPosition());
        this.telemetry.addData("Left Count ", leftDrive.getCurrentPosition());
        this.telemetry.addData("Shoulder Count ", shoulder.getCurrentPosition());
        this.telemetry.addData("Elbow Count ", elbow.getCurrentPosition());

    }

    private void PowerSetter() throws Exception {
        this.rightDrive.setPower(rightY);
        this.leftDrive.setPower(-leftY);
        this.shoulder.setPower(-shoulderPower);
        this.elbow.setPower(-elbowPower);
    }
}
