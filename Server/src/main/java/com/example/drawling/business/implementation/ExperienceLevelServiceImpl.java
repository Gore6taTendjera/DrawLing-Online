package com.example.drawling.business.implementation;

import com.example.drawling.business.interfaces.repository.ExperienceLevelRepository;
import com.example.drawling.business.interfaces.service.ExperienceLevelService;
import com.example.drawling.domain.model.profile.ExperienceLevel;
import org.springframework.stereotype.Service;


@Service
public class ExperienceLevelServiceImpl implements ExperienceLevelService {

    private final ExperienceLevelRepository experienceLevelRepository;
    private static final String USER_ID_MUST_BE_GREATER_THAN_ZERO = "User id must be greater than zero.";

    public ExperienceLevelServiceImpl(ExperienceLevelRepository experienceLevelRepository) {
        this.experienceLevelRepository = experienceLevelRepository;
    }

    public int getTotalExperienceByUserId(int userId) {
        return experienceLevelRepository.getTotalExperience(userId);
    }

    public int setExperienceLevelByUserId(int userId, double amount) {
        if (userId <= 0) {
            throw new IllegalArgumentException(USER_ID_MUST_BE_GREATER_THAN_ZERO);
        }

        return experienceLevelRepository.setExperienceLevelByUserId(userId, amount);
    }

    public int addExperienceLevelByUserId(int userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Experience level amount must be greater than zero.");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException(USER_ID_MUST_BE_GREATER_THAN_ZERO);
        }

        return experienceLevelRepository.addExperienceLevelByUserId(userId, amount);
    }


    public ExperienceLevel getExperienceLevelByUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException(USER_ID_MUST_BE_GREATER_THAN_ZERO);
        }

        int userXp = experienceLevelRepository.getTotalExperience(userId);

        int userLevel = getUserLevel(userXp);

        int minLvlPoints = getXpForLevel(userLevel);
        int maxLvlPoints = getXpForLevel(userLevel + 1);

        return new ExperienceLevel(userLevel, maxLvlPoints - userXp, minLvlPoints, maxLvlPoints);
    }

    public int getXpForLevel(int level) {
        if (level == 0) {
            return 0; // level 0
        } else if (level >= 1000) {
            return 10000 + (level - 100) * 200; // > 1000
        } else if (level >= 100) {
            return 1000 + (level - 100) * 200; // 100 - 999
        } else if (level >= 10) {
            return 100 + (level - 10) * 100; // 10 - 99
        } else {
            return level * 10; // 1 - 9
        }
    }

    public int getUserLevel(int userXp) {
        int maxLevel = 1000;

        if (userXp >= getXpForLevel(maxLevel)) {
            return maxLevel;
        }

        int low = 1;
        int high = maxLevel;

        while (low <= high) {
            int mid = (low + high) / 2;
            int midXp = getXpForLevel(mid);
            int nextLevelXp = getXpForLevel(mid + 1);

            if (userXp >= midXp && userXp < nextLevelXp) {
                return mid;
            } else if (userXp < midXp) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return 0;
    }

}
