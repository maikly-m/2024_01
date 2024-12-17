package com.example.u.dialog

import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.example.u.R
import com.example.u.databinding.DialogConfirmBinding
import com.example.u.uitls.DisplayUtils

class ConfirmDialog : BaseDialogFragment<DialogConfirmBinding>() {
    private var mTitle: String? = null
    private var mMessage: String? = null
    private var mSpannableMessage: SpannableString? = null
    private var mPositiveText: String? = null
    private var mNegativeText: String? = null
    private var mPositiveListener: OnDialogClickListener? = null
    private var mNegativeListener: OnDialogClickListener? = null
    private var mPortrait = false
    private var mDrawableId = 0
    private var mTitleSize = 0
    private var mMsgSize = 0
    private var mButtonSize = 0

    companion object{
        val PORTRAIT_FLAG: String = "portrait_flag"
        fun newInstance(portrait: Boolean): ConfirmDialog {
            val dialog = ConfirmDialog()
            val bundle = Bundle()
            bundle.putBoolean(PORTRAIT_FLAG, portrait)
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        if (arguments != null) {
            mPortrait = arguments.getBoolean(PORTRAIT_FLAG)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_confirm
    }

    override fun initView() {
        if (!TextUtils.isEmpty(mTitle)) {
            mBinding.tvTitle.visibility = View.VISIBLE
            mBinding.tvTitle.text = mTitle
        }
        if (!TextUtils.isEmpty(mMessage)) {
            mBinding.tvMessage.visibility = View.VISIBLE
            mBinding.tvMessage.text = mMessage
        }
        if (!TextUtils.isEmpty(mSpannableMessage)) {
            mBinding.tvMessage.visibility = View.VISIBLE
            mBinding.tvMessage.text = mSpannableMessage
            mBinding.tvMessage.movementMethod = LinkMovementMethod.getInstance()
        }

        mBinding.positive.visibility = View.GONE
        mBinding.negative.visibility = View.GONE
        if (mPositiveText != null) {
            mBinding.positive.text = mPositiveText
            mBinding.positive.visibility = View.VISIBLE
        }
        if (mNegativeText != null) {
            mBinding.negative.text = mNegativeText
            mBinding.negative.visibility = View.VISIBLE
        }
        if (mPositiveText != null && mNegativeText != null) {
            mBinding.marginTopStub.visibility = View.VISIBLE
        } else {
            mBinding.marginTopStub.visibility = View.GONE
        }
        if (mTitleSize > 0) {
            mBinding.tvTitle.textSize = mTitleSize.toFloat()
        }
        if (mMsgSize > 0) {
            mBinding.tvMessage.textSize = mMsgSize.toFloat()
        }
        if (mButtonSize > 0) {
            mBinding.positive.textSize = mButtonSize.toFloat()
            mBinding.negative.textSize = mButtonSize.toFloat()
        }
        setDimAmount(0.7f)
        if (mPortrait) {
            // setWidthPercent(0.8f);
            // 宽度300dp
            setWidth(DisplayUtils.dp2px(requireContext(), 300F))
        } else {
            // setWidthPercent(0.4f);
            // 宽度300dp
            setWidth(DisplayUtils.dp2px(requireContext(), 300F))
        }
        if (mDrawableId != 0) {
            mBinding.clBg.background = ResourcesCompat.getDrawable(resources, mDrawableId, null)
        }

        mBinding.positive.setOnClickListener {
            if (mPositiveListener != null) {
                mPositiveListener!!.onClick(this@ConfirmDialog)
            } else {
                dismissAllowingStateLoss()
            }
        }

        mBinding.negative.setOnClickListener {
            if (mNegativeListener != null) {
                mNegativeListener!!.onClick(this@ConfirmDialog)
            }
        }

        paddingTop = DisplayUtils.dp2px(requireContext(), 16f)
        paddingBottom = DisplayUtils.dp2px(requireContext(), 16f)
    }

    fun setBgColor(@DrawableRes drawableId: Int): ConfirmDialog {
        this.mDrawableId = drawableId
        return this
    }

    fun setTitle(title: String): ConfirmDialog {
        this.mTitle = title
        return this
    }

    fun setMessage(message: String): ConfirmDialog {
        this.mMessage = message
        return this
    }

    fun setMessage(spannableString: SpannableString): ConfirmDialog {
        this.mSpannableMessage = spannableString
        return this
    }

    fun setDimValue(mDimAmount: Float): ConfirmDialog {
        setDimAmount(mDimAmount)
        return this
    }

    fun setNegativeButton(text: String, negativeListener: OnDialogClickListener): ConfirmDialog {
        this.mNegativeText = text
        this.mNegativeListener = negativeListener
        return this
    }

    fun setPositiveButton(text: String, positiveListener: OnDialogClickListener?): ConfirmDialog {
        this.mPositiveText = text
        this.mPositiveListener = positiveListener
        return this
    }

    fun setCancelableOut(canCancel: Boolean): ConfirmDialog {
        setOutCancel(canCancel)
        return this
    }

    fun setForceFullScreenBool(fullScreenBool: Boolean): ConfirmDialog {
        setForceFullScreen(fullScreenBool)
        return this
    }

    fun setTextSize(titleSize: Int, msgSize: Int, buttonSize: Int): ConfirmDialog {
        this.mTitleSize = titleSize
        this.mMsgSize = msgSize
        this.mButtonSize = buttonSize
        return this
    }

    interface OnDialogClickListener {
        fun onClick(dialog: DialogFragment)
    }
}