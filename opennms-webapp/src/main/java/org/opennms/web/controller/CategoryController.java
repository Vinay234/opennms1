package org.opennms.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opennms.netmgt.model.OnmsCategory;
import org.opennms.web.svclayer.AdminCategoryService;
import org.opennms.web.svclayer.support.DefaultAdminCategoryService.EditModel;
import org.opennms.web.svclayer.support.DefaultAdminCategoryService.NodeEditModel;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class CategoryController extends AbstractController {

    private AdminCategoryService m_adminCategoryService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OnmsCategory category = null;
        
        String removeCategoryIdString = request.getParameter("removeCategoryId");
        String newCategoryName = request.getParameter("newCategoryName");
        String categoryIdString = request.getParameter("categoryid");
        String editString = request.getParameter("edit");
        String nodeIdString = request.getParameter("node");
        
        if (removeCategoryIdString != null) {
            m_adminCategoryService.removeCategory(removeCategoryIdString);
            
            List<OnmsCategory> sortedCategories
                = m_adminCategoryService.findAllCategories();
        
            return new ModelAndView("/admin/categories",
                                    "categories",
                                    sortedCategories);
        }
        
        if (newCategoryName != null) {
            OnmsCategory newCategory =
                m_adminCategoryService.addNewCategory(newCategoryName);
            
            EditModel model =
                m_adminCategoryService.findCategoryAndAllNodes(Integer.toString(newCategory.getId()));

            return new ModelAndView("/admin/editCategory",
                                    "model",
                                    model);
        }
        
        if (categoryIdString != null && editString != null) {
            String editAction = request.getParameter("action");
            if (editAction != null) {
                String[] toAdd = request.getParameterValues("toAdd");
                String[] toDelete = request.getParameterValues("toDelete");

                m_adminCategoryService.performEdit(categoryIdString,
                                                      editAction,
                                                      toAdd,
                                                      toDelete);
            }

            EditModel model =
                m_adminCategoryService.findCategoryAndAllNodes(categoryIdString);

            return new ModelAndView("/admin/editCategory",
                                    "model",
                                    model);
        }
        
        if (categoryIdString != null) {
            category = m_adminCategoryService.getCategory(categoryIdString);
            return new ModelAndView("/admin/showCategory",
                                    "category",
                                    category);
        }
        
        if (nodeIdString != null && editString != null) {
            String editAction = request.getParameter("action");
            if (editAction != null) {
                String[] toAdd = request.getParameterValues("toAdd");
                String[] toDelete = request.getParameterValues("toDelete");

                m_adminCategoryService.performNodeEdit(nodeIdString,
                                                       editAction,
                                                       toAdd,
                                                       toDelete);
            }

            NodeEditModel model =
                m_adminCategoryService.findNodeCategories(nodeIdString);

            return new ModelAndView("/admin/editNodeCategories",
                                    "model",
                                    model);
        }


        List<OnmsCategory> sortedCategories
            = m_adminCategoryService.findAllCategories();
        
        return new ModelAndView("/admin/categories",
                                "categories",
                                sortedCategories);
    }

    public AdminCategoryService getAdminCategoryService() {
        return m_adminCategoryService;
    }

    public void setAdminCategoryService(
            AdminCategoryService adminCategoryService) {
        m_adminCategoryService = adminCategoryService;
    }

}
