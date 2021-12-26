package com.dongkap.master.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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
@EqualsAndHashCode(callSuper=false, exclude={"b2bs", "assets"})
@ToString(exclude={"b2bs", "assets"})
@Entity
@Table(name = "mst_business_partner", schema = SchemaDatabase.MASTER)
public class BusinessPartnerEntity extends BaseAuditEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1932022761237540822L;

	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(name = "bp_uuid", nullable = false, unique = true)
	private String id;

	@Column(name = "bp_name", nullable = false)
	private String bpName;

	@Column(name = "email")
	private String email;

	@Column(name = "address")
	private String address;

	@Column(name = "telp_number")
	private String telpNumber;

	@Column(name = "fax_number")
	private String faxNumber;

	@ManyToMany(mappedBy = "businessPartner", targetEntity = B2BEntity.class, fetch = FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	private Set<B2BEntity> b2bs = new HashSet<B2BEntity>();

	@ManyToMany(mappedBy = "businessPartner", targetEntity = AssetEntity.class, fetch = FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	private Set<AssetEntity> assets = new HashSet<AssetEntity>();

}