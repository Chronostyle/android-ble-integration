package com.sample.bleintegration

object Opcode {
    val WritePrintTemperature = 0x0012
    val ReadPrintTemperature = 0x0013
    val ReadPrintHeadId = 0x0014
    val ReadDeviceInfo = 0x0200
    val Restart = 0x0201
    val UpdateMcu = 0x0203
    val WriteLogoData = 0x0204
    val WritePrintParameters = 0x0002
    val TransmitPictureData = 0x0100
    val PrintPicture = 0x0300
    val ReadBattery = 0x0018
    val ReadRechargeState = 0x0019
    val ReadBluetoothConnectState = 0x0202
    val ReadPrinterParameters = 0x0003
    val WriteCirculationAndRepeatTimes = 0x0005
    val ReadCirculationAndRepeatTimes = 0x0006
    val WritePrintDirection = 0x0007
    val ReadPrintDirection = 0x0008
}