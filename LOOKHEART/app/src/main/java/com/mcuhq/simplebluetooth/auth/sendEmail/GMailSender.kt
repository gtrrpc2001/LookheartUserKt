package com.mcuhq.simplebluetooth.auth.sendEmail

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Properties
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.activation.DataSource
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class GMailSender : Authenticator() {
    private val host = "smtp.gmail.com"
    private val sendEmail = "medsyslab.2017@gmail.com"
    private val pwd = "lkfq vlhg sgzm fcjw"
    private val session: Session

    //생성된 이메일 인증코드 반환
    @JvmField
    val emailCode: String
    private val mailScheduler = Executors.newScheduledThreadPool(1)

    init {
        emailCode = createEmailCode()
        val key = 465
        val prop = Properties()
        prop.setProperty("mail.transport.protocol", "smtp")
        prop["mail.host"] = host
        prop["mail.smtp.auth"] = "true"
        prop["mail.smtp.port"] = "587"
        prop["mail.smtp.socketFactory.port"] = "587"
        prop["mail.smtp.socketFactory.fallback"] = "false"
        prop["mail.smtp.starttls.enable"] = "true"
        prop.setProperty("mail.smtp.quitwait", "false")
        prop["mail.smtp.ssl.protocols"] = "TLSv1.2"
        session = Session.getDefaultInstance(prop, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                //해당 메서드에서 사용자의 계정(id & password)을 받아 인증받으며 인증 실패시 기본값으로 반환됨.
                return PasswordAuthentication(sendEmail, pwd)
            }
        })
    }

    private fun createEmailCode(): String { //이메일 인증코드 생성
        val str = arrayOf(
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g",
            "h",
            "i",
            "j",
            "k",
            "l",
            "m",
            "n",
            "o",
            "p",
            "q",
            "r",
            "s",
            "t",
            "u",
            "v",
            "w",
            "x",
            "y",
            "z",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9"
        )
        var newCode = String()
        for (x in 0..7) {
            val random = (Math.random() * str.size).toInt()
            newCode += str[random]
        }
        return newCode
    }

    @Synchronized
    @Throws(Exception::class)
    fun sendMail(subject: String?, body: String?, recipients: String?) {
        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(sendEmail, "(주)MSL"))
            message.addRecipient(Message.RecipientType.TO, InternetAddress(recipients))
            //DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain")); //본문 내용을 byte단위로 쪼개어 전달
            message.sender = InternetAddress(sendEmail) //본인 이메일 설정
            message.subject = subject //해당 이메일의 본문 설정
            message.setContent(body, "text/plain")
            message.setText(body)
            mailScheduler.schedule({
                try {
                    Transport.send(message, message.allRecipients) // 메시지 전달
                } catch (e: MessagingException) {
                    throw RuntimeException(e)
                } finally {
                    mailScheduler.shutdown()
                }
            }, 0, TimeUnit.SECONDS)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

    inner class ByteArrayDataSource : DataSource {
        private var data: ByteArray
        private var type: String? = null

        constructor(data: ByteArray, type: String?) : super() {
            this.data = data
            this.type = type
        }

        constructor(data: ByteArray) : super() {
            this.data = data
        }

        fun setType(type: String?) {
            this.type = type
        }

        override fun getContentType(): String {
            return type ?: "application/octet-stream"
        }

        @Throws(IOException::class)
        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(data)
        }

        override fun getName(): String {
            return "ByteArrayDataSource"
        }

        @Throws(IOException::class)
        override fun getOutputStream(): OutputStream {
            throw IOException("Not Supported")
        }
    }
}