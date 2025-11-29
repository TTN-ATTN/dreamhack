package org.springframework.web.servlet.function;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import kotlin.Metadata;
import kotlin.PublishedApi;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.servlet.tags.BindTag;

/* compiled from: RouterFunctionDsl.kt */
@Metadata(mv = {1, 1, 18}, bv = {1, 0, 3}, k = 1, d1 = {"��\u009a\u0001\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0003\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\t\u0018��2\u00020\u0001B \b��\u0012\u0017\u0010\u0002\u001a\u0013\u0012\u0004\u0012\u00020��\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\b\u0005¢\u0006\u0002\u0010\u0006J\u001a\u0010\r\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\r\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\r\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\r\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\r\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010\u0015\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010\u0016\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010\u0017\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010\u0018\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010\u0019\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\u0019\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010\u001a\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001f\u0010\u001b\u001a\u00020\u00112\u0012\u0010\u001c\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u001e0\u001d\"\u00020\u001e¢\u0006\u0002\u0010\u001fJ\"\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u001c\u001a\u00020\u001e2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u0006\u0010 \u001a\u00020!J\u0014\u0010\"\u001a\u00020\u00042\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00100$J \u0010%\u001a\u00020\u00042\u0018\u0010&\u001a\u0014\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100'J\u0006\u0010(\u001a\u00020!J\u001a\u0010)\u001a\u00020\u00042\u0012\u0010*\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f0\u0003J\u0013\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00100$H��¢\u0006\u0002\b,J\u001f\u0010-\u001a\u00020\u00112\u0012\u0010.\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u001e0\u001d\"\u00020\u001e¢\u0006\u0002\u0010\u001fJ\"\u0010-\u001a\u00020\u00042\u0006\u0010\u001c\u001a\u00020\u001e2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010/\u001a\u00020!2\u0006\u00100\u001a\u000201J,\u00102\u001a\u00020\u00042$\u00103\u001a \u0012\u0004\u0012\u00020\u000f\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003\u0012\u0004\u0012\u00020\u00100'J\u000e\u00104\u001a\u00020!2\u0006\u00105\u001a\u00020\u0010J\u001a\u00106\u001a\u00020\u00112\u0012\u00107\u001a\u000e\u0012\u0004\u0012\u000208\u0012\u0004\u0012\u0002090\u0003J.\u00106\u001a\u00020\u00042\u0012\u00107\u001a\u000e\u0012\u0004\u0012\u000208\u0012\u0004\u0012\u0002090\u00032\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010:\u001a\u00020\u00112\u0006\u0010;\u001a\u00020<J\"\u0010:\u001a\u00020\u00042\u0006\u0010;\u001a\u00020<2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\n\u0010=\u001a\u0006\u0012\u0002\b\u00030>J\n\u0010?\u001a\u0006\u0012\u0002\b\u00030>J\u0006\u0010@\u001a\u00020!J4\u0010A\u001a\u00020\u00042\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020B\u0012\u0004\u0012\u0002090\u00032\u0018\u0010C\u001a\u0014\u0012\u0004\u0012\u00020B\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100'J1\u0010A\u001a\u00020\u0004\"\n\b��\u0010D\u0018\u0001*\u00020B2\u001a\b\b\u0010C\u001a\u0014\u0012\u0004\u0012\u00020B\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100'H\u0086\bJ\"\u0010E\u001a\u00020\u00112\u0006\u0010F\u001a\u00020\u00132\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u0002090\u0003J6\u0010E\u001a\u00020\u00042\u0006\u0010F\u001a\u00020\u00132\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u0002090\u00032\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010G\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010G\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010H\u001a\u00020\u00112\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u0002090\u0003J.\u0010H\u001a\u00020\u00042\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u0002090\u00032\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010H\u001a\u00020\u00112\u0006\u0010I\u001a\u00020\u0013J\"\u0010H\u001a\u00020\u00042\u0006\u0010I\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010J\u001a\u00020!2\u0006\u00100\u001a\u000201J\u001c\u0010K\u001a\u00020\u00042\u0014\u0010L\u001a\u0010\u0012\u0004\u0012\u00020\u000f\u0012\u0006\u0012\u0004\u0018\u00010M0\u0003J\u0016\u0010K\u001a\u00020\u00042\u0006\u0010G\u001a\u00020\u00132\u0006\u00100\u001a\u00020MJ\u000e\u0010N\u001a\u00020!2\u0006\u00100\u001a\u000201J\u000e\u0010O\u001a\u00020!2\u0006\u0010O\u001a\u00020PJ\u000e\u0010O\u001a\u00020!2\u0006\u0010O\u001a\u00020QJ\u000e\u0010R\u001a\u00020!2\u0006\u00100\u001a\u000201J\u0006\u0010S\u001a\u00020!J\u0015\u0010T\u001a\u00020\u0011*\u00020\u00132\u0006\u00105\u001a\u00020\u0011H\u0086\u0004J\u0015\u0010T\u001a\u00020\u0011*\u00020\u00112\u0006\u00105\u001a\u00020\u0013H\u0086\u0004J\u0015\u0010T\u001a\u00020\u0011*\u00020\u00112\u0006\u00105\u001a\u00020\u0011H\u0086\u0004J!\u0010U\u001a\u00020\u0004*\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003H\u0086\u0002J!\u0010U\u001a\u00020\u0004*\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003H\u0086\u0002J#\u0010V\u001a\u00020\u0004*\u00020\u00132\u0017\u0010W\u001a\u0013\u0012\u0004\u0012\u00020��\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\b\u0005J#\u0010V\u001a\u00020\u0004*\u00020\u00112\u0017\u0010W\u001a\u0013\u0012\u0004\u0012\u00020��\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\b\u0005J\r\u0010X\u001a\u00020\u0011*\u00020\u0011H\u0086\u0002J\u0015\u0010Y\u001a\u00020\u0011*\u00020\u00132\u0006\u00105\u001a\u00020\u0011H\u0086\u0004J\u0015\u0010Y\u001a\u00020\u0011*\u00020\u00112\u0006\u00105\u001a\u00020\u0013H\u0086\u0004J\u0015\u0010Y\u001a\u00020\u0011*\u00020\u00112\u0006\u00105\u001a\u00020\u0011H\u0086\u0004R\u001c\u0010\u0007\u001a\u00020\b8��X\u0081\u0004¢\u0006\u000e\n��\u0012\u0004\b\t\u0010\n\u001a\u0004\b\u000b\u0010\fR\u001f\u0010\u0002\u001a\u0013\u0012\u0004\u0012\u00020��\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\b\u0005X\u0082\u0004¢\u0006\u0002\n��¨\u0006Z"}, d2 = {"Lorg/springframework/web/servlet/function/RouterFunctionDsl;", "", "init", "Lkotlin/Function1;", "", "Lkotlin/ExtensionFunctionType;", "(Lkotlin/jvm/functions/Function1;)V", "builder", "Lorg/springframework/web/servlet/function/RouterFunctions$Builder;", "builder$annotations", "()V", "getBuilder", "()Lorg/springframework/web/servlet/function/RouterFunctions$Builder;", "DELETE", "f", "Lorg/springframework/web/servlet/function/ServerRequest;", "Lorg/springframework/web/servlet/function/ServerResponse;", "Lorg/springframework/web/servlet/function/RequestPredicate;", "pattern", "", "predicate", "GET", WebContentGenerator.METHOD_HEAD, "OPTIONS", "PATCH", WebContentGenerator.METHOD_POST, "PUT", "accept", "mediaType", "", "Lorg/springframework/http/MediaType;", "([Lorg/springframework/http/MediaType;)Lorg/springframework/web/servlet/function/RequestPredicate;", "accepted", "Lorg/springframework/web/servlet/function/ServerResponse$BodyBuilder;", BeanUtil.PREFIX_ADDER, "routerFunction", "Lorg/springframework/web/servlet/function/RouterFunction;", "after", "responseProcessor", "Lkotlin/Function2;", "badRequest", "before", "requestProcessor", JsonPOJOBuilder.DEFAULT_BUILD_METHOD, "build$spring_webmvc", "contentType", "mediaTypes", "created", "location", "Ljava/net/URI;", "filter", "filterFunction", "from", "other", "headers", "headersPredicate", "Lorg/springframework/web/servlet/function/ServerRequest$Headers;", "", "method", "httpMethod", "Lorg/springframework/http/HttpMethod;", "noContent", "Lorg/springframework/web/servlet/function/ServerResponse$HeadersBuilder;", "notFound", "ok", "onError", "", "responseProvider", "E", "param", "name", "path", "pathExtension", "extension", "permanentRedirect", "resources", "lookupFunction", "Lorg/springframework/core/io/Resource;", "seeOther", BindTag.STATUS_VARIABLE_NAME, "", "Lorg/springframework/http/HttpStatus;", "temporaryRedirect", "unprocessableEntity", "and", "invoke", "nest", "r", "not", "or", "spring-webmvc"})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RouterFunctionDsl.class */
public final class RouterFunctionDsl {

    @NotNull
    private final RouterFunctions.Builder builder;
    private final Function1<RouterFunctionDsl, Unit> init;

    @PublishedApi
    public static /* synthetic */ void builder$annotations() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public RouterFunctionDsl(@NotNull Function1<? super RouterFunctionDsl, Unit> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "init");
        this.init = function1;
        RouterFunctions.Builder builderRoute = RouterFunctions.route();
        Intrinsics.checkExpressionValueIsNotNull(builderRoute, "RouterFunctions.route()");
        this.builder = builderRoute;
    }

    @NotNull
    public final RouterFunctions.Builder getBuilder() {
        return this.builder;
    }

    @NotNull
    public final RequestPredicate and(@NotNull RequestPredicate $this$and, @NotNull String other) {
        Intrinsics.checkParameterIsNotNull($this$and, "$this$and");
        Intrinsics.checkParameterIsNotNull(other, "other");
        RequestPredicate requestPredicateAnd = $this$and.and(path(other));
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateAnd, "this.and(path(other))");
        return requestPredicateAnd;
    }

    @NotNull
    public final RequestPredicate or(@NotNull RequestPredicate $this$or, @NotNull String other) {
        Intrinsics.checkParameterIsNotNull($this$or, "$this$or");
        Intrinsics.checkParameterIsNotNull(other, "other");
        RequestPredicate requestPredicateOr = $this$or.or(path(other));
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateOr, "this.or(path(other))");
        return requestPredicateOr;
    }

    @NotNull
    public final RequestPredicate and(@NotNull String $this$and, @NotNull RequestPredicate other) {
        Intrinsics.checkParameterIsNotNull($this$and, "$this$and");
        Intrinsics.checkParameterIsNotNull(other, "other");
        RequestPredicate requestPredicateAnd = path($this$and).and(other);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateAnd, "path(this).and(other)");
        return requestPredicateAnd;
    }

    @NotNull
    public final RequestPredicate or(@NotNull String $this$or, @NotNull RequestPredicate other) {
        Intrinsics.checkParameterIsNotNull($this$or, "$this$or");
        Intrinsics.checkParameterIsNotNull(other, "other");
        RequestPredicate requestPredicateOr = path($this$or).or(other);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateOr, "path(this).or(other)");
        return requestPredicateOr;
    }

    @NotNull
    public final RequestPredicate and(@NotNull RequestPredicate $this$and, @NotNull RequestPredicate other) {
        Intrinsics.checkParameterIsNotNull($this$and, "$this$and");
        Intrinsics.checkParameterIsNotNull(other, "other");
        RequestPredicate requestPredicateAnd = $this$and.and(other);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateAnd, "this.and(other)");
        return requestPredicateAnd;
    }

    @NotNull
    public final RequestPredicate or(@NotNull RequestPredicate $this$or, @NotNull RequestPredicate other) {
        Intrinsics.checkParameterIsNotNull($this$or, "$this$or");
        Intrinsics.checkParameterIsNotNull(other, "other");
        RequestPredicate requestPredicateOr = $this$or.or(other);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateOr, "this.or(other)");
        return requestPredicateOr;
    }

    @NotNull
    public final RequestPredicate not(@NotNull RequestPredicate $this$not) {
        Intrinsics.checkParameterIsNotNull($this$not, "$this$not");
        RequestPredicate requestPredicateNegate = $this$not.negate();
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateNegate, "this.negate()");
        return requestPredicateNegate;
    }

    /* compiled from: RouterFunctionDsl.kt */
    @Metadata(mv = {1, 1, 18}, bv = {1, 0, 3}, k = 3, d1 = {"��\f\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\u0010��\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001¢\u0006\u0002\b\u0003"}, d2 = {"<anonymous>", "Lorg/springframework/web/servlet/function/RouterFunction;", "Lorg/springframework/web/servlet/function/ServerResponse;", "invoke"})
    /* renamed from: org.springframework.web.servlet.function.RouterFunctionDsl$nest$1, reason: invalid class name and case insensitive filesystem */
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RouterFunctionDsl$nest$1.class */
    static final /* synthetic */ class C00071 extends FunctionReference implements Function0<RouterFunction<ServerResponse>> {
        public final KDeclarationContainer getOwner() {
            return Reflection.getOrCreateKotlinClass(RouterFunctionDsl.class);
        }

        public final String getName() {
            return JsonPOJOBuilder.DEFAULT_BUILD_METHOD;
        }

        public final String getSignature() {
            return "build$spring_webmvc()Lorg/springframework/web/servlet/function/RouterFunction;";
        }

        C00071(RouterFunctionDsl routerFunctionDsl) {
            super(0, routerFunctionDsl);
        }

        @NotNull
        public final RouterFunction<ServerResponse> invoke() {
            return ((RouterFunctionDsl) this.receiver).build$spring_webmvc();
        }
    }

    public final void nest(@NotNull RequestPredicate $this$nest, @NotNull Function1<? super RouterFunctionDsl, Unit> function1) {
        Intrinsics.checkParameterIsNotNull($this$nest, "$this$nest");
        Intrinsics.checkParameterIsNotNull(function1, "r");
        RouterFunctions.Builder builder = this.builder;
        final C00071 c00071 = new C00071(new RouterFunctionDsl(function1));
        builder.nest($this$nest, new Supplier() { // from class: org.springframework.web.servlet.function.RouterFunctionDslKt$sam$java_util_function_Supplier$0
            @Override // java.util.function.Supplier
            public final /* synthetic */ Object get() {
                return c00071.invoke();
            }
        });
    }

    public final void nest(@NotNull String $this$nest, @NotNull Function1<? super RouterFunctionDsl, Unit> function1) {
        Intrinsics.checkParameterIsNotNull($this$nest, "$this$nest");
        Intrinsics.checkParameterIsNotNull(function1, "r");
        nest(path($this$nest), function1);
    }

    public final void GET(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.GET(new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void GET(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.GET(pattern, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void GET(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.GET(predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void GET(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.GET(pattern, predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    @NotNull
    public final RequestPredicate GET(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        RequestPredicate requestPredicateGET = RequestPredicates.GET(pattern);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateGET, "RequestPredicates.GET(pattern)");
        return requestPredicateGET;
    }

    public final void HEAD(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.HEAD(new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void HEAD(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.HEAD(pattern, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void HEAD(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.HEAD(predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void HEAD(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.HEAD(pattern, predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    @NotNull
    public final RequestPredicate HEAD(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        RequestPredicate requestPredicateHEAD = RequestPredicates.HEAD(pattern);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateHEAD, "RequestPredicates.HEAD(pattern)");
        return requestPredicateHEAD;
    }

    public final void POST(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.POST(new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void POST(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.POST(pattern, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void POST(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.POST(predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void POST(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.POST(pattern, predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    @NotNull
    public final RequestPredicate POST(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        RequestPredicate requestPredicatePOST = RequestPredicates.POST(pattern);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicatePOST, "RequestPredicates.POST(pattern)");
        return requestPredicatePOST;
    }

    public final void PUT(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.PUT(new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void PUT(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.PUT(pattern, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void PUT(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.PUT(predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void PUT(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.PUT(pattern, predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    @NotNull
    public final RequestPredicate PUT(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        RequestPredicate requestPredicatePUT = RequestPredicates.PUT(pattern);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicatePUT, "RequestPredicates.PUT(pattern)");
        return requestPredicatePUT;
    }

    public final void PATCH(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.PATCH(new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void PATCH(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.PATCH(pattern, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void PATCH(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.PATCH(predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void PATCH(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.PATCH(pattern, predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    @NotNull
    public final RequestPredicate PATCH(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        RequestPredicate requestPredicatePATCH = RequestPredicates.PATCH(pattern);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicatePATCH, "RequestPredicates.PATCH(pattern)");
        return requestPredicatePATCH;
    }

    public final void DELETE(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.DELETE(new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void DELETE(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.DELETE(pattern, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void DELETE(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.DELETE(predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void DELETE(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.DELETE(pattern, predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    @NotNull
    public final RequestPredicate DELETE(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        RequestPredicate requestPredicateDELETE = RequestPredicates.DELETE(pattern);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateDELETE, "RequestPredicates.DELETE(pattern)");
        return requestPredicateDELETE;
    }

    public final void OPTIONS(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.OPTIONS(new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void OPTIONS(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.OPTIONS(pattern, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void OPTIONS(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.OPTIONS(predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    public final void OPTIONS(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.OPTIONS(pattern, predicate, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1));
    }

    @NotNull
    public final RequestPredicate OPTIONS(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        RequestPredicate requestPredicateOPTIONS = RequestPredicates.OPTIONS(pattern);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateOPTIONS, "RequestPredicates.OPTIONS(pattern)");
        return requestPredicateOPTIONS;
    }

    public final void accept(@NotNull MediaType mediaType, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(mediaType, "mediaType");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.accept(mediaType), new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1)));
    }

    @NotNull
    public final RequestPredicate accept(@NotNull MediaType... mediaType) {
        Intrinsics.checkParameterIsNotNull(mediaType, "mediaType");
        RequestPredicate requestPredicateAccept = RequestPredicates.accept((MediaType[]) Arrays.copyOf(mediaType, mediaType.length));
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateAccept, "RequestPredicates.accept(*mediaType)");
        return requestPredicateAccept;
    }

    public final void contentType(@NotNull MediaType mediaType, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(mediaType, "mediaType");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.contentType(mediaType), new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1)));
    }

    @NotNull
    public final RequestPredicate contentType(@NotNull MediaType... mediaTypes) {
        Intrinsics.checkParameterIsNotNull(mediaTypes, "mediaTypes");
        RequestPredicate requestPredicateContentType = RequestPredicates.contentType((MediaType[]) Arrays.copyOf(mediaTypes, mediaTypes.length));
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateContentType, "RequestPredicates.contentType(*mediaTypes)");
        return requestPredicateContentType;
    }

    public final void headers(@NotNull Function1<? super ServerRequest.Headers, Boolean> function1, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function12) {
        Intrinsics.checkParameterIsNotNull(function1, "headersPredicate");
        Intrinsics.checkParameterIsNotNull(function12, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.headers(new RouterFunctionDslKt$sam$java_util_function_Predicate$0(function1)), new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function12)));
    }

    @NotNull
    public final RequestPredicate headers(@NotNull Function1<? super ServerRequest.Headers, Boolean> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "headersPredicate");
        RequestPredicate requestPredicateHeaders = RequestPredicates.headers(new RouterFunctionDslKt$sam$java_util_function_Predicate$0(function1));
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateHeaders, "RequestPredicates.headers(headersPredicate)");
        return requestPredicateHeaders;
    }

    public final void method(@NotNull HttpMethod httpMethod, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(httpMethod, "httpMethod");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.method(httpMethod), new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1)));
    }

    @NotNull
    public final RequestPredicate method(@NotNull HttpMethod httpMethod) {
        Intrinsics.checkParameterIsNotNull(httpMethod, "httpMethod");
        RequestPredicate requestPredicateMethod = RequestPredicates.method(httpMethod);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateMethod, "RequestPredicates.method(httpMethod)");
        return requestPredicateMethod;
    }

    public final void path(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.path(pattern), new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1)));
    }

    @NotNull
    public final RequestPredicate path(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull(pattern, "pattern");
        RequestPredicate requestPredicatePath = RequestPredicates.path(pattern);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicatePath, "RequestPredicates.path(pattern)");
        return requestPredicatePath;
    }

    public final void pathExtension(@NotNull String extension, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull(extension, "extension");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.pathExtension(extension), new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1)));
    }

    @NotNull
    public final RequestPredicate pathExtension(@NotNull String extension) {
        Intrinsics.checkParameterIsNotNull(extension, "extension");
        RequestPredicate requestPredicatePathExtension = RequestPredicates.pathExtension(extension);
        Intrinsics.checkExpressionValueIsNotNull(requestPredicatePathExtension, "RequestPredicates.pathExtension(extension)");
        return requestPredicatePathExtension;
    }

    public final void pathExtension(@NotNull Function1<? super String, Boolean> function1, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function12) {
        Intrinsics.checkParameterIsNotNull(function1, "predicate");
        Intrinsics.checkParameterIsNotNull(function12, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.pathExtension(new RouterFunctionDslKt$sam$java_util_function_Predicate$0(function1)), new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function12)));
    }

    @NotNull
    public final RequestPredicate pathExtension(@NotNull Function1<? super String, Boolean> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "predicate");
        RequestPredicate requestPredicatePathExtension = RequestPredicates.pathExtension(new RouterFunctionDslKt$sam$java_util_function_Predicate$0(function1));
        Intrinsics.checkExpressionValueIsNotNull(requestPredicatePathExtension, "RequestPredicates.pathExtension(predicate)");
        return requestPredicatePathExtension;
    }

    public final void param(@NotNull String name, @NotNull Function1<? super String, Boolean> function1, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function12) {
        Intrinsics.checkParameterIsNotNull(name, "name");
        Intrinsics.checkParameterIsNotNull(function1, "predicate");
        Intrinsics.checkParameterIsNotNull(function12, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.param(name, new RouterFunctionDslKt$sam$java_util_function_Predicate$0(function1)), new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function12)));
    }

    @NotNull
    public final RequestPredicate param(@NotNull String name, @NotNull Function1<? super String, Boolean> function1) {
        Intrinsics.checkParameterIsNotNull(name, "name");
        Intrinsics.checkParameterIsNotNull(function1, "predicate");
        RequestPredicate requestPredicateParam = RequestPredicates.param(name, new RouterFunctionDslKt$sam$java_util_function_Predicate$0(function1));
        Intrinsics.checkExpressionValueIsNotNull(requestPredicateParam, "RequestPredicates.param(name, predicate)");
        return requestPredicateParam;
    }

    public final void invoke(@NotNull RequestPredicate $this$invoke, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull($this$invoke, "$this$invoke");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.add(RouterFunctions.route($this$invoke, new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1)));
    }

    public final void invoke(@NotNull String $this$invoke, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> function1) {
        Intrinsics.checkParameterIsNotNull($this$invoke, "$this$invoke");
        Intrinsics.checkParameterIsNotNull(function1, "f");
        this.builder.add(RouterFunctions.route(RequestPredicates.path($this$invoke), new RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(function1)));
    }

    public final void resources(@NotNull String path, @NotNull Resource location) {
        Intrinsics.checkParameterIsNotNull(path, "path");
        Intrinsics.checkParameterIsNotNull(location, "location");
        this.builder.resources(path, location);
    }

    public final void resources(@NotNull final Function1<? super ServerRequest, ? extends Resource> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "lookupFunction");
        this.builder.resources(new Function() { // from class: org.springframework.web.servlet.function.RouterFunctionDsl.resources.1
            @Override // java.util.function.Function
            @NotNull
            public final Optional<Resource> apply(ServerRequest it) {
                Function1 function12 = function1;
                Intrinsics.checkExpressionValueIsNotNull(it, "it");
                return Optional.ofNullable(function12.invoke(it));
            }
        });
    }

    public final void add(@NotNull RouterFunction<ServerResponse> routerFunction) {
        Intrinsics.checkParameterIsNotNull(routerFunction, "routerFunction");
        this.builder.add(routerFunction);
    }

    public final void filter(@NotNull final Function2<? super ServerRequest, ? super Function1<? super ServerRequest, ? extends ServerResponse>, ? extends ServerResponse> function2) {
        Intrinsics.checkParameterIsNotNull(function2, "filterFunction");
        this.builder.filter(new HandlerFilterFunction() { // from class: org.springframework.web.servlet.function.RouterFunctionDsl.filter.1
            @Override // org.springframework.web.servlet.function.HandlerFilterFunction
            @NotNull
            public final ServerResponse filter(@NotNull ServerRequest request, @NotNull final HandlerFunction<ServerResponse> handlerFunction) {
                Intrinsics.checkParameterIsNotNull(request, "request");
                Intrinsics.checkParameterIsNotNull(handlerFunction, "next");
                return (ServerResponse) function2.invoke(request, new Function1<ServerRequest, ServerResponse>() { // from class: org.springframework.web.servlet.function.RouterFunctionDsl.filter.1.1
                    {
                        super(1);
                    }

                    @NotNull
                    public final ServerResponse invoke(@NotNull ServerRequest handlerRequest) throws Exception {
                        Intrinsics.checkParameterIsNotNull(handlerRequest, "handlerRequest");
                        ServerResponse serverResponseHandle = handlerFunction.handle(handlerRequest);
                        Intrinsics.checkExpressionValueIsNotNull(serverResponseHandle, "next.handle(handlerRequest)");
                        return serverResponseHandle;
                    }
                });
            }
        });
    }

    public final void before(@NotNull final Function1<? super ServerRequest, ? extends ServerRequest> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "requestProcessor");
        this.builder.before(new Function() { // from class: org.springframework.web.servlet.function.RouterFunctionDslKt$sam$java_util_function_Function$0
            @Override // java.util.function.Function
            public final /* synthetic */ Object apply(Object p0) {
                return function1.invoke(p0);
            }
        });
    }

    public final void after(@NotNull Function2<? super ServerRequest, ? super ServerResponse, ? extends ServerResponse> function2) {
        Intrinsics.checkParameterIsNotNull(function2, "responseProcessor");
        this.builder.after(new RouterFunctionDslKt$sam$java_util_function_BiFunction$0(function2));
    }

    public final void onError(@NotNull Function1<? super Throwable, Boolean> function1, @NotNull Function2<? super Throwable, ? super ServerRequest, ? extends ServerResponse> function2) {
        Intrinsics.checkParameterIsNotNull(function1, "predicate");
        Intrinsics.checkParameterIsNotNull(function2, "responseProvider");
        this.builder.onError(new RouterFunctionDslKt$sam$java_util_function_Predicate$0(function1), new RouterFunctionDslKt$sam$java_util_function_BiFunction$0(function2));
    }

    public final /* synthetic */ <E extends Throwable> void onError(final Function2<? super Throwable, ? super ServerRequest, ? extends ServerResponse> function2) {
        Intrinsics.checkParameterIsNotNull(function2, "responseProvider");
        RouterFunctions.Builder builder = getBuilder();
        Intrinsics.needClassReification();
        builder.onError(new Predicate() { // from class: org.springframework.web.servlet.function.RouterFunctionDsl.onError.1
            @Override // java.util.function.Predicate
            public final boolean test(Throwable it) {
                Intrinsics.reifiedOperationMarker(3, "E");
                return it instanceof Throwable;
            }
        }, new BiFunction() { // from class: org.springframework.web.servlet.function.RouterFunctionDslKt$sam$i$java_util_function_BiFunction$0
            @Override // java.util.function.BiFunction
            public final /* synthetic */ Object apply(Object p0, Object p1) {
                return function2.invoke(p0, p1);
            }
        });
    }

    @NotNull
    public final RouterFunction<ServerResponse> build$spring_webmvc() {
        this.init.invoke(this);
        RouterFunction<ServerResponse> routerFunctionBuild = this.builder.build();
        Intrinsics.checkExpressionValueIsNotNull(routerFunctionBuild, "builder.build()");
        return routerFunctionBuild;
    }

    @NotNull
    public final ServerResponse.BodyBuilder from(@NotNull ServerResponse other) {
        Intrinsics.checkParameterIsNotNull(other, "other");
        ServerResponse.BodyBuilder bodyBuilderFrom = ServerResponse.from(other);
        Intrinsics.checkExpressionValueIsNotNull(bodyBuilderFrom, "ServerResponse.from(other)");
        return bodyBuilderFrom;
    }

    @NotNull
    public final ServerResponse.BodyBuilder created(@NotNull URI location) {
        Intrinsics.checkParameterIsNotNull(location, "location");
        ServerResponse.BodyBuilder bodyBuilderCreated = ServerResponse.created(location);
        Intrinsics.checkExpressionValueIsNotNull(bodyBuilderCreated, "ServerResponse.created(location)");
        return bodyBuilderCreated;
    }

    @NotNull
    public final ServerResponse.BodyBuilder ok() {
        ServerResponse.BodyBuilder bodyBuilderOk = ServerResponse.ok();
        Intrinsics.checkExpressionValueIsNotNull(bodyBuilderOk, "ServerResponse.ok()");
        return bodyBuilderOk;
    }

    @NotNull
    public final ServerResponse.HeadersBuilder<?> noContent() {
        ServerResponse.HeadersBuilder<?> headersBuilderNoContent = ServerResponse.noContent();
        Intrinsics.checkExpressionValueIsNotNull(headersBuilderNoContent, "ServerResponse.noContent()");
        return headersBuilderNoContent;
    }

    @NotNull
    public final ServerResponse.BodyBuilder accepted() {
        ServerResponse.BodyBuilder bodyBuilderAccepted = ServerResponse.accepted();
        Intrinsics.checkExpressionValueIsNotNull(bodyBuilderAccepted, "ServerResponse.accepted()");
        return bodyBuilderAccepted;
    }

    @NotNull
    public final ServerResponse.BodyBuilder permanentRedirect(@NotNull URI location) {
        Intrinsics.checkParameterIsNotNull(location, "location");
        ServerResponse.BodyBuilder bodyBuilderPermanentRedirect = ServerResponse.permanentRedirect(location);
        Intrinsics.checkExpressionValueIsNotNull(bodyBuilderPermanentRedirect, "ServerResponse.permanentRedirect(location)");
        return bodyBuilderPermanentRedirect;
    }

    @NotNull
    public final ServerResponse.BodyBuilder temporaryRedirect(@NotNull URI location) {
        Intrinsics.checkParameterIsNotNull(location, "location");
        ServerResponse.BodyBuilder bodyBuilderTemporaryRedirect = ServerResponse.temporaryRedirect(location);
        Intrinsics.checkExpressionValueIsNotNull(bodyBuilderTemporaryRedirect, "ServerResponse.temporaryRedirect(location)");
        return bodyBuilderTemporaryRedirect;
    }

    @NotNull
    public final ServerResponse.BodyBuilder seeOther(@NotNull URI location) {
        Intrinsics.checkParameterIsNotNull(location, "location");
        ServerResponse.BodyBuilder bodyBuilderSeeOther = ServerResponse.seeOther(location);
        Intrinsics.checkExpressionValueIsNotNull(bodyBuilderSeeOther, "ServerResponse.seeOther(location)");
        return bodyBuilderSeeOther;
    }

    @NotNull
    public final ServerResponse.BodyBuilder badRequest() {
        ServerResponse.BodyBuilder bodyBuilderBadRequest = ServerResponse.badRequest();
        Intrinsics.checkExpressionValueIsNotNull(bodyBuilderBadRequest, "ServerResponse.badRequest()");
        return bodyBuilderBadRequest;
    }

    @NotNull
    public final ServerResponse.HeadersBuilder<?> notFound() {
        ServerResponse.HeadersBuilder<?> headersBuilderNotFound = ServerResponse.notFound();
        Intrinsics.checkExpressionValueIsNotNull(headersBuilderNotFound, "ServerResponse.notFound()");
        return headersBuilderNotFound;
    }

    @NotNull
    public final ServerResponse.BodyBuilder unprocessableEntity() {
        ServerResponse.BodyBuilder bodyBuilderUnprocessableEntity = ServerResponse.unprocessableEntity();
        Intrinsics.checkExpressionValueIsNotNull(bodyBuilderUnprocessableEntity, "ServerResponse.unprocessableEntity()");
        return bodyBuilderUnprocessableEntity;
    }

    @NotNull
    public final ServerResponse.BodyBuilder status(@NotNull HttpStatus status) {
        Intrinsics.checkParameterIsNotNull(status, BindTag.STATUS_VARIABLE_NAME);
        ServerResponse.BodyBuilder bodyBuilderStatus = ServerResponse.status(status);
        Intrinsics.checkExpressionValueIsNotNull(bodyBuilderStatus, "ServerResponse.status(status)");
        return bodyBuilderStatus;
    }

    @NotNull
    public final ServerResponse.BodyBuilder status(int status) {
        ServerResponse.BodyBuilder bodyBuilderStatus = ServerResponse.status(status);
        Intrinsics.checkExpressionValueIsNotNull(bodyBuilderStatus, "ServerResponse.status(status)");
        return bodyBuilderStatus;
    }
}
