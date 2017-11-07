/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/nicola/dev/ar-github/AndroidRipper/src/it/unina/android/ripper_service/IAndroidRipperService.aidl
 */
package it.unina.android.ripper_service;
public interface IAndroidRipperService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements it.unina.android.ripper_service.IAndroidRipperService
{
private static final java.lang.String DESCRIPTOR = "it.unina.android.ripper_service.IAndroidRipperService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an it.unina.android.ripper_service.IAndroidRipperService interface,
 * generating a proxy if needed.
 */
public static it.unina.android.ripper_service.IAndroidRipperService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof it.unina.android.ripper_service.IAndroidRipperService))) {
return ((it.unina.android.ripper_service.IAndroidRipperService)iin);
}
return new it.unina.android.ripper_service.IAndroidRipperService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_send:
{
data.enforceInterface(DESCRIPTOR);
java.util.Map _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
this.send(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_register:
{
data.enforceInterface(DESCRIPTOR);
it.unina.android.ripper_service.IAnrdoidRipperServiceCallback _arg0;
_arg0 = it.unina.android.ripper_service.IAnrdoidRipperServiceCallback.Stub.asInterface(data.readStrongBinder());
this.register(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregister:
{
data.enforceInterface(DESCRIPTOR);
it.unina.android.ripper_service.IAnrdoidRipperServiceCallback _arg0;
_arg0 = it.unina.android.ripper_service.IAnrdoidRipperServiceCallback.Stub.asInterface(data.readStrongBinder());
this.unregister(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getForegroundProcess:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getForegroundProcess();
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements it.unina.android.ripper_service.IAndroidRipperService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void send(java.util.Map message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(message);
mRemote.transact(Stub.TRANSACTION_send, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void register(it.unina.android.ripper_service.IAnrdoidRipperServiceCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_register, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregister(it.unina.android.ripper_service.IAnrdoidRipperServiceCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregister, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getForegroundProcess() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getForegroundProcess, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_send = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_register = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_unregister = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getForegroundProcess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void send(java.util.Map message) throws android.os.RemoteException;
public void register(it.unina.android.ripper_service.IAnrdoidRipperServiceCallback cb) throws android.os.RemoteException;
public void unregister(it.unina.android.ripper_service.IAnrdoidRipperServiceCallback cb) throws android.os.RemoteException;
public java.lang.String getForegroundProcess() throws android.os.RemoteException;
}
