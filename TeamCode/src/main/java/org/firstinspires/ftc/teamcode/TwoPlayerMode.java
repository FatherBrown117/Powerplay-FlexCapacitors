package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "TwoPlayerMode")

public class TwoPlayerMode extends LinearOpMode {

    BasicAuto obj = new BasicAuto();

    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor leftRear = null;
    private DcMotor rightRear = null;
    private DcMotor rightArm = null;
    private DcMotor leftArm = null;
    private Servo clawServo = null;
    //private Servo servo2 = null;

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftFront = hardwareMap.get(DcMotor.class,"leftFront"); //frontleft, port 0
        rightFront = hardwareMap.get(DcMotor.class,"rightFront");  //frontright, port 1
        leftRear = hardwareMap.get(DcMotor.class,"leftRear"); //backleft, port 3
        rightRear = hardwareMap.get(DcMotor.class,"rightRear");  //backright, port 2
        rightArm = hardwareMap.get(DcMotor.class, "rightArm");
        leftArm = hardwareMap.get(DcMotor.class, "leftArm");

        clawServo = hardwareMap.get(Servo.class, "clawServo");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        leftRear.setDirection(DcMotor.Direction.REVERSE);
        rightRear.setDirection(DcMotor.Direction.FORWARD);
        rightArm.setDirection(DcMotorSimple.Direction.REVERSE);

        leftArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //motor6.setDirection(DcMotor.Direction.REVERSE);


        waitForStart();

        while (opModeIsActive()) {

            /*
            double m1Power;
            double m2Power;
            double m3Power;
            double m4Power;
            //double m5Power;
            //double m6Power;

            m1Power = -gamepad1.left_stick_y/1.5;
            m2Power = -gamepad1.right_stick_y/1.5;
            m3Power = -gamepad1.left_stick_y/1.5;
            m4Power = -gamepad1.right_stick_y/1.5;
            // m5Power = gamepad2.left_stick_y;
            // m6Power = -gamepad1.right_trigger+gamepad1.left_trigger;

            frontLeft.setPower(m1Power);
            frontRight.setPower(m2Power);
            backLeft.setPower(m3Power);
            backRight.setPower(m4Power);
            //motor5.setPower(m5Power);
            //motor6.setPower(m6Power);
            */

            double G1rightStickY = gamepad1.right_stick_y;
            double G1leftStickY = gamepad1.left_stick_y;
            double G1rightStickX = gamepad1.right_stick_x;
            double G1leftStickX = gamepad1.left_stick_x;
            boolean G1rightBumper = gamepad1.right_bumper;
            boolean G1leftBumper = gamepad1.left_bumper;
            boolean G1Y = gamepad1.y;
            boolean G1B = gamepad1.b;
            boolean G1X = gamepad1.x;
            boolean G1A = gamepad1.a;
            double G1RT = -gamepad1.right_trigger;
            double G1LT = gamepad1.left_trigger;

            //claw movements
            if (gamepad2.a){
                clawServo.setPosition(.4);// opens claw
            } else if (gamepad2.b) {
                clawServo.setPosition(0.95); //closes claw
            } else if (gamepad2.x) {
                rightArm.setPower(0); //stops arm
                leftArm.setPower(0);
            }

            telemetry.update();

            //Driving movements
            if (G1rightStickX > 0) {  // Clockwise Slow
                leftFront.setPower(0.5);
                leftRear.setPower(0.5);
                rightFront.setPower(-0.5);
                rightRear.setPower(-0.5);
            } else if (G1rightStickX < 0) { // Counterclockwise Slow
                leftFront.setPower(-0.5);
                leftRear.setPower(-0.5);
                rightFront.setPower(0.5);
                rightRear.setPower(0.5);
            } else if (G1leftStickY > 0) { //backwards
                leftFront.setPower(-0.5);
                leftRear.setPower(-0.5);
                rightFront.setPower(-0.5);
                rightRear.setPower(-0.5);
            } else if (G1leftStickY < 0) { //forwards
                leftFront.setPower(.5);
                leftRear.setPower(.5);
                rightFront.setPower(.5);
                rightRear.setPower(.5);
            } else if (gamepad1.dpad_right) { //strafe right
                leftFront.setPower(1);
                rightFront.setPower(-1);
                leftRear.setPower(-1);
                rightRear.setPower(1);
            } else if (gamepad1.dpad_left) { //strafe left
                leftFront.setPower(-1);
                rightFront.setPower(1);
                leftRear.setPower(1);
                rightRear.setPower(-1);
            } else if (gamepad1.dpad_up) {
                leftFront.setPower(0.5);
                rightFront.setPower(0.5);
                leftRear.setPower(0.5);
                rightRear.setPower(0.5);
            } else if (gamepad1.dpad_down) {
                leftFront.setPower(-0.5);
                rightFront.setPower(-0.5);
                leftRear.setPower(-0.5);
                rightRear.setPower(-0.5);
            } else if (G1rightBumper) {
                leftFront.setPower(.5);
                rightFront.setPower(-.5);
                leftRear.setPower(-.5);
                rightRear.setPower(.5);

            } else if (G1leftBumper) {
                leftFront.setPower(-.5);
                rightFront.setPower(.5);
                leftRear.setPower(.5);
                rightRear.setPower(-.5);

            } else {
                leftFront.setPower(0);
                rightFront.setPower(0);
                leftRear.setPower(0);
                rightRear.setPower(0);
            }

            if (gamepad2.right_bumper) {
                rightArm.setPower(0.5);
                leftArm.setPower(0.5);
            } else if (gamepad2.left_bumper) {
                rightArm.setPower(-0.3);
                leftArm.setPower(-0.3);
            }



            telemetry.addData("Status", "Running");
            telemetry.update();

        }
    }

    public void armUp(double distance) {

        //Reset Encoders
        rightArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rightArm.setPower(0.45);

        while (rightArm.getCurrentPosition() < distance) {
            telemetry.addData("Arm Encoder", rightArm.getCurrentPosition());
            telemetry.update();
        }

        rightArm.setPower(0);

        sleep(500);

    }

    public void armDown(double distance) {

        //Reset Encoders
        rightArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rightArm.setPower(-0.45);

        while (-rightArm.getCurrentPosition() < distance) {
            telemetry.addData("Arm Encoder", rightArm.getCurrentPosition());
            telemetry.update();
        }

        rightArm.setPower(0);

        sleep(500);

    }

}