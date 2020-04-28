package net.vesseldoc.server.service;

import net.vesseldoc.server.model.Form;
import net.vesseldoc.server.model.FormStructure;
import net.vesseldoc.server.repository.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.*;

@Service
public class FormService {

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FormStructureService formStructureService;

    /**
     * This creates a new form object, sends the information to the database and returns and ID.
     *
     * @param userId      User ID
     * @param structureId Form structure ID
     * @return Form ID of the newly created form
     */
    public UUID save(long userId, long structureId) {
        Form form = new Form();
        form.setUser_id(userId);
        form.setForm_structure_id(structureId);

        formRepository.save(form);
        return getLatestFormByUser(userId);
    }

    /**
     * Gets the latest created Form ID by the given user.
     *
     * @param userId User ID
     * @return Form ID
     */
    public UUID getLatestFormByUser(long userId) {
        byte[] uuidAsBytes = formRepository.getLatestFormId(userId);
        ByteBuffer b = ByteBuffer.wrap(uuidAsBytes);
        UUID uuid = new UUID(b.getLong(), b.getLong());
        return uuid;
    }


    public ResponseEntity<List<List<Object>>> getAllForms() {
        List<Form> dbContent = formRepository.getAll();
        List<List<Object>> list = new ArrayList<List<Object>>();
        Iterator<Form> it = dbContent.iterator();
        while (it.hasNext()) {
            Form fs = it.next();
            String structureName = formStructureService.getFormStructure(fs.getForm_structure_id()).getTitle();
            String structureOwner = userService.getUserDetails(fs.getUser_id()).getUsername();
            list.add(Arrays.asList( structureName, structureOwner, fs));
        }
        return ResponseEntity.ok(list);
    }

    public ResponseEntity<List<List<Object>>> getAllUnsigned() {
        List<Form> dbContent = formRepository.getAll();
        List<List<Object>> list = new ArrayList<List<Object>>();
        Iterator<Form> it = dbContent.iterator();
        while (it.hasNext()) {
            Form fs = it.next();
            if (!fs.isSigned()) {
                String structureName = formStructureService.getFormStructure(fs.getForm_structure_id()).getTitle();
                String structureOwner = userService.getUserDetails(fs.getUser_id()).getUsername();
                list.add(Arrays.asList( structureName, structureOwner, fs));
            }
        }
        return ResponseEntity.ok(list);
    }

    public ResponseEntity<List<List<Object>>> getAllFormsByUser(long userId) {
        List<Form> dbContent = formRepository.getAllByUserId(userId);
        List<List<Object>> list = new ArrayList<List<Object>>();
        Iterator<Form> it = dbContent.iterator();
        while (it.hasNext()) {
            Form fs = it.next();
            String structureName = formStructureService.getFormStructure(fs.getForm_structure_id()).getTitle();
            String structureOwner = userService.getUserDetails(fs.getUser_id()).getUsername();
            list.add(Arrays.asList( structureName, structureOwner, fs));
        }
        return ResponseEntity.ok(list);

    }

    public ResponseEntity<List<List<Object>>> getAllUnsignedByUser(long userId) {
        List<Form> dbContent = formRepository.getAllByUserId(userId);
        List<List<Object>> list = new ArrayList<List<Object>>();
        Iterator<Form> it = dbContent.iterator();
        while (it.hasNext()) {
            Form fs = it.next();
            if (!fs.isSigned()) {
                String structureName = formStructureService.getFormStructure(fs.getForm_structure_id()).getTitle();
                String structureOwner = userService.getUserDetails(fs.getUser_id()).getUsername();
                list.add(Arrays.asList( structureName, structureOwner, fs));
            }
        }
        return ResponseEntity.ok(list);
    }

    public Form getForm(String uuid) {
        return formRepository.getById(UUID.fromString(uuid));
    }

    public ResponseEntity<String> signForm(String formId) {
        ResponseEntity<String> response;
        Form form = getForm(formId);

        if (!userService.getUserRole(userService.getCurrentUser().getUsername()).equals("ADMIN")) {
            response = ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not permitted to do this!");
        } else if (form.isSigned()) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body("Form is already signed.");
        } else if (!fileService.formExists(formId)) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body("Form is not filled yet.");
        } else {
            form.setSigned(true);
            form.setSignedUserId(userService.getCurrentUser().getId());
            formRepository.save(form);
            response = ResponseEntity.ok("Successfully signed the form!");
        }
        return response;
    }

    public boolean isSigned(String formId) {
        return getForm(formId).isSigned();
    }
}
