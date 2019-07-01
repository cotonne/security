-- From https://docs.spring.io/spring-security/site/docs/current/reference/html/advanced-topics.html#domain-acls
-- ACL_SID allows us to uniquely identify a principal or authority
-- in the system ("SID" stands for "security identity"
-- Columns are:
--  * ID
--  * sid: a textual representation (username)
--  * principal: flag to indicate whether the sid refers to
--                (1) a principal name or
--                (0) a GrantedAuthority
-- In the context of receiving a permission, a SID is generally called a "recipient"
INSERT INTO  acl_sid(id, principal, sid) VALUES
    (11, 1, 'user'),
    (12, 1, 'other_user'),
    (13, 0, 'a_group');

-- Classes to protect
INSERT INTO acl_class (id, class) VALUES
    (42, 'com.example.demoacl.domain.Hello');

-- object_id_class: FK to acl_class
-- owner_sid: FK to acl_sid
-- object_id_identity: id of the object to protect
INSERT INTO acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) VALUES
 (1, 42, 101, NULL, 11, 0 ),
 (2, 42, 102, NULL, 12, 0 );

-- ACL_ENTRY stores the individual permissions assigned to each recipient
-- Columns are
--  * acl_object_identity: FK to ACL_OBJECT_IDENTITY (acl_object_identity.id=acl_entry.acl_object_identity)
--  * sid: the recipient (FK to ACL_SID)
--  * audit_success: whether we'll be auditing or not
--  * mask: represents the actual permission being granted or denied
--      READ(1), WRITE(2), CHANGE?(4), DELETE(8), A....(16)
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) VALUES
( 1, 1, 1, 11, 1, 1, 1, 1),
( 2, 2, 1, 12, 1, 1, 1, 1);
