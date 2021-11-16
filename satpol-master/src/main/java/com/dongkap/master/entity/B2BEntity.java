package com.dongkap.master.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.dongkap.common.utils.SchemaDatabase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false, exclude={"businessPartner", "corporate"})
@ToString(exclude={"businessPartner", "corporate"})
@Entity
@Table(name = "mst_b2b", schema = SchemaDatabase.MASTER)
public class B2BEntity extends BaseAuditEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1932022761237540822L;

	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(name = "b2b_uuid", nullable = false, unique = true)
	private String id;

	@Column(name = "b2b_non_expired", nullable = false)
	private boolean b2bNonExpired = true;

	@Column(name = "b2b_expired_time")
	private Date expiredTime;

	@OneToOne(targetEntity = BusinessPartnerEntity.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "bp_uuid", nullable = false)
	private BusinessPartnerEntity businessPartner;

	@OneToOne(targetEntity = CorporateEntity.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "corporate_uuid", nullable = false)
	private CorporateEntity corporate;

}