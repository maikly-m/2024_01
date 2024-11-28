package com.example.u.net.exception


class ResultErr(var errCode: String?, var msg: String?) : Exception(msg)
