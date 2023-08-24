package com.te.flinko.util;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.UpdateResult;

@Service
public class Generator {

	@Autowired
	private MongoTemplate template;

	public Long generateSequence(String seqName) {
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where("_id").is(seqName)));
		SequenceGenerator sequenceGenerator = template.findAndModify(query, new Update().inc("sequenceId", 1),
				options().returnNew(true).upsert(true), SequenceGenerator.class);
		return !Objects.isNull(sequenceGenerator) ? sequenceGenerator.getSequenceId() : 1;
	}

	public Long updateSenerateSequence(String seqName) {
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where("_id").is(seqName)));
		SequenceGenerator sequenceGenerator = template.findAndModify(query, new Update().set("sequenceId", 0),
				options().returnNew(true).upsert(true), SequenceGenerator.class);
		return !Objects.isNull(sequenceGenerator) ? sequenceGenerator.getSequenceId() : 1;
	}

	public String updateIdentificationSequence(String seqName) {
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where("_id").is(seqName)));
		IdentificationSequenceGenerator generator = template.findOne(query, IdentificationSequenceGenerator.class);
		IdentificationSequenceGenerator sequenceGenerator = template.findAndModify(query,
				new Update().set("sequenceId",
						generateIdentificationNumber(generator == null ? "AA0A0AA00AA" : generator.getSequenceId())),
				options().returnNew(true).upsert(true), IdentificationSequenceGenerator.class);
		return !Objects.isNull(sequenceGenerator) ? sequenceGenerator.getSequenceId() : "AA0A0AA00AA";
	}

	public Long idGenerator(Long deptId, String seqName) {
		Long sequence = generateSequence(seqName);
		if (sequence.equals(999l) || sequence >= 999) {
			updateSenerateSequence(seqName);
		}
		LocalDate date = LocalDate.now();
		int monthValue = date.getMonthValue();
		return Long.parseLong(
				date.getYear() + "" + String.format("%0" + (3 - ("" + monthValue).length()) + "d", monthValue) + ""
						+ String.format("%0" + (3 - ("" + deptId).length()) + "d", deptId) + ""
						+ String.format("%0" + (4 - ("" + sequence).length()) + "d", sequence));
	}

	public String generateIdentificationNumber(String num) {
		char[] ch = num.toCharArray();
		int carry = 1;
		for (int i = ch.length - 1; i >= 0; i--) {
			if (ch[i] != 57 && ch[i] != 90) {
				ch[i] = (char) (ch[i] + carry);
				break;
			}
			ch[i] = ch[i] == 57 ? '0' : 'A';
		}
		return new String(ch);
	}

}
