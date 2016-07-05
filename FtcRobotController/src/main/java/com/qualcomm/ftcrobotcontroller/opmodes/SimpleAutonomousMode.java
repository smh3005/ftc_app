package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by smh30 on 6/3/2016.
 */
public class SimpleAutonomousMode extends OpMode {

    DcMotor leftDrive;
    DcMotor rightDrive;
    DcMotor shoulder;
    DcMotor elbow;

    enum State {liftArm, drivingFoward}
    State state;

    int leftTargetPosition = 5356;
    int rightTargetPosition = -5356;

    int shoulderTargetPosition;

    double leftSpeed = 0.2;
    double rightSpeed = -0.2;

    @Override
    public void init() {
        try {
            initializeAutonomousMode();
        } catch (Exception e) {
            DbgLog.error("Seth - Initialization Robot Core Exception Error");
        }
    }

    @Override
    public void loop() {
        switch (state) {
            case liftArm:
                this.shoulder.setTargetPosition(shoulderTargetPosition);
                this.shoulder.setPower(0.25);

                if (shoulder.getCurrentPosition() > shoulderTargetPosition) {
                    state = state.drivingFoward;
                    shoulder.setPower(0);
                }
            case drivingFoward:
                leftDrive.setTargetPosition(leftTargetPosition);
                rightDrive.setTargetPosition(rightTargetPosition);
                //leftDrive.setPower(Range.clip
                //(leftSpeed *(leftTargetPosition-this.leftDrive.getCurrentPosition())/leftTargetPosition, -1, 1));
                //rightDrive.setPower(rightSpeed * (rightTargetPosition/this.rightDrive.getCurrentPosition()));

                leftDrive.setPower(leftSpeed);
                rightDrive.setPower(rightSpeed);

                if (leftDrive.getCurrentPosition() >= leftTargetPosition) {
                    leftDrive.setPower(0);
                }

                if (rightDrive.getCurrentPosition() <= rightTargetPosition) {
                    rightDrive.setPower(0);
                }

                if (leftDrive.getCurrentPosition() >= leftTargetPosition
                        && rightDrive.getCurrentPosition() <= rightTargetPosition) {
                    stop();
                }

                break;
        }
    }

    @Override
    public void stop() {
        this.rightDrive.setPower(0);
        this.leftDrive.setPower(0);
        this.shoulder.setPower(0);
        this.elbow.setPower(0);
    }

    private void initializeAutonomousMode() {
        //get references to the hardware from the hardware map
        this.leftDrive = hardwareMap.dcMotor.get("leftDrive");
        this.rightDrive = hardwareMap.dcMotor.get("rightDrive");

        this.shoulder = hardwareMap.dcMotor.get("shoulder");
        this.elbow = hardwareMap.dcMotor.get("elbow");

        this.leftDrive.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.rightDrive.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.shoulder.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        this.elbow.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        state = state.liftArm;

        shoulderTargetPosition = 800;

    }


}
