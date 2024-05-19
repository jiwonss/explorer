using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CompoundList : MonoBehaviour
{
    private List<Dictionary<string, object>> compoundList;

    public static CompoundList Instance { get; private set; }

    private void Awake()
    {
        if (Instance == null)
        {
            Instance = this;
            DontDestroyOnLoad(gameObject);
        }
        else
        {
            Destroy(gameObject);
        }

        InitializeCompoundList();
    }

    private void InitializeCompoundList()
    {
        compoundList = new List<Dictionary<string, object>>
        {
            new Dictionary<string, object>
            {
                { "key", 3 },
                { "name", "O2" },
                { "image", "Images/Compounds/O2"},
                { "summary", "산소 \n[구성] O: 2" }
            },
            new Dictionary<string, object>
            {
                { "key", 4 },
                { "name", "NH3" },
                { "image", "Images/Compounds/NH3"},
                { "summary", "암모니아 \n[구성] N: 1, H: 3" }
            },
            new Dictionary<string, object>
            {
                { "key", 5 },
                { "name", "CuS" },
                { "image", "Images/Compounds/CuS"},
                { "summary", "황화구리 \n[구성] Cu: 1, S: 1" }
            },
            new Dictionary<string, object>
            {
                { "key", 6 },
                { "name", "C2H6" },
                { "image", "Images/Compounds/C2H6"},
                { "summary", "에탄 \n[구성] C: 2, H: 6" }
            },
            new Dictionary<string, object>
            {
                { "key", 7 },
                { "name", "H2O2" },
                { "image", "Images/Compounds/H2O2"},
                { "summary", "과산화수소 \n[구성] H: 2, O: 2" }
            },
            new Dictionary<string, object>
            {
                { "key", 8 },
                { "name", "Al2O3" },
                { "image", "Images/Compounds/Al2O3"},
                { "summary", "산화알루미늄 \n[구성] Al: 2, O: 3" }
            },
            new Dictionary<string, object>
            {
                { "key", 9 },
                { "name", "AgCO3" },
                { "image", "Images/Compounds/AgCO3"},
                { "summary", "탄산은 \n[구성] Ag: 1, C: 1, O: 3" }
            },
            new Dictionary<string, object>
            {
                { "key", 10 },
                { "name", "C2H6" },
                { "image", "Images/Compounds/C2H66"},
                { "summary", "메탄 \n[구성] C: 2, H: 6" }
            },
            new Dictionary<string, object>
            {
                { "key", 11 },
                { "name", "FeSO4" },
                { "image", "Images/Compounds/FeSO4"},
                { "summary", "황산철 \n[구성] Fe: 1, S: 1, O: 4" }
            },
            new Dictionary<string, object>
            {
                { "key", 12 },
                { "name", "CuSO4" },
                { "image", "Images/Compounds/CuSO4"},
                { "summary", "황산구리 \n[구성] Cu: 1, S: 1, O: 4" }
            },
            new Dictionary<string, object>
            {
                { "key", 13 },
                { "name", "H2O" },
                { "image", "Images/Compounds/CuSO4"},
                { "summary", "물 \n[구성] H: 2, O: 2" }
            },
            new Dictionary<string, object>
            {
                { "key", 14 },
                { "name", "NHO3" },
                { "image", "Images/Compounds/CuSO4"},
                { "summary", "질산 \n[구성] N: 1, H: 1, O: 3" }
            },
            new Dictionary<string, object>
            {
                { "key", 15 },
                { "name", "NH4NO3" },
                { "image", "Images/Compounds/CuSO4"},
                { "summary", "질산암모늄 \n[구성] N: 2, H: 4, O: 3" }
            }
        };
    }

    public List<Dictionary<string, object>> GetCompoundList()
    {
        return compoundList;
    }
}
