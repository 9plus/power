/*
 * Copyright (c) 2003, 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package jdk.javadoc.internal.doclets.toolkit.builders;

import java.util.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import jdk.javadoc.internal.doclets.toolkit.BaseConfiguration;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.DocletException;
import jdk.javadoc.internal.doclets.toolkit.FieldWriter;
import jdk.javadoc.internal.doclets.toolkit.util.VisibleMemberMap;


/**
 * Builds documentation for a field.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 *
 * @author Jamie Ho
 * @author Bhavesh Patel (Modified)
 */
public class FieldBuilder extends AbstractMemberBuilder {

    /**
     * The class whose fields are being documented.
     */
    private final TypeElement typeElement;

    /**
     * The visible fields for the given class.
     */
    private final VisibleMemberMap visibleMemberMap;

    /**
     * The writer to output the field documentation.
     */
    private final FieldWriter writer;

    /**
     * The list of fields being documented.
     */
    private final List<Element> fields;

    /**
     * The index of the current field that is being documented at this point
     * in time.
     */
    private VariableElement currentElement;

    /**
     * Construct a new FieldBuilder.
     *
     * @param context  the build context.
     * @param typeElement the class whoses members are being documented.
     * @param writer the doclet specific writer.
     */
    private FieldBuilder(Context context,
            TypeElement typeElement,
            FieldWriter writer) {
        super(context);
        this.typeElement = typeElement;
        this.writer = writer;
        visibleMemberMap = configuration.getVisibleMemberMap(typeElement,
                VisibleMemberMap.Kind.FIELDS);
        fields = visibleMemberMap.getLeafMembers();
    }

    /**
     * Construct a new FieldBuilder.
     *
     * @param context  the build context.
     * @param typeElement the class whoses members are being documented.
     * @param writer the doclet specific writer.
     * @return the new FieldBuilder
     */
    public static FieldBuilder getInstance(Context context,
            TypeElement typeElement,
            FieldWriter writer) {
        return new FieldBuilder(context, typeElement, writer);
    }

    /**
     * Returns whether or not there are members to document.
     *
     * @return whether or not there are members to document
     */
    @Override
    public boolean hasMembersToDocument() {
        return !fields.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void build(Content contentTree) throws DocletException {
        buildFieldDoc(contentTree);
    }

    /**
     * Build the field documentation.
     *
     * @param memberDetailsTree the content tree to which the documentation will be added
     * @throws DocletException if there is a problem while building the documentation
     */
    protected void buildFieldDoc(Content memberDetailsTree) throws DocletException {
        if (writer == null) {
            return;
        }
        if (!fields.isEmpty()) {
            Content fieldDetailsTree = writer.getFieldDetailsTreeHeader(typeElement, memberDetailsTree);

            Element lastElement = fields.get(fields.size() - 1);
            for (Element element : fields) {
                currentElement = (VariableElement)element;
                Content fieldDocTree = writer.getFieldDocTreeHeader(currentElement, fieldDetailsTree);

                buildSignature(fieldDocTree);
                buildDeprecationInfo(fieldDocTree);
                buildFieldComments(fieldDocTree);
                buildTagInfo(fieldDocTree);

                fieldDetailsTree.addContent(writer.getFieldDoc(
                        fieldDocTree, currentElement == lastElement));
            }
            memberDetailsTree.addContent(
                    writer.getFieldDetails(fieldDetailsTree));
        }
    }

    /**
     * Build the signature.
     *
     * @param fieldDocTree the content tree to which the documentation will be added
     */
    protected void buildSignature(Content fieldDocTree) {
        fieldDocTree.addContent(writer.getSignature(currentElement));
    }

    /**
     * Build the deprecation information.
     *
     * @param fieldDocTree the content tree to which the documentation will be added
     */
    protected void buildDeprecationInfo(Content fieldDocTree) {
        writer.addDeprecated(currentElement, fieldDocTree);
    }

    /**
     * Build the comments for the field.  Do nothing if
     * {@link BaseConfiguration#nocomment} is set to true.
     *
     * @param fieldDocTree the content tree to which the documentation will be added
     */
    protected void buildFieldComments(Content fieldDocTree) {
        if (!configuration.nocomment) {
            writer.addComments(currentElement, fieldDocTree);
        }
    }

    /**
     * Build the tag information.
     *
     * @param fieldDocTree the content tree to which the documentation will be added
     */
    protected void buildTagInfo(Content fieldDocTree) {
        writer.addTags(currentElement, fieldDocTree);
    }

    /**
     * Return the field writer for this builder.
     *
     * @return the field writer for this builder.
     */
    public FieldWriter getWriter() {
        return writer;
    }
}
