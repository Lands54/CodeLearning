import torch
class M(torch.nn.Module):
    def __init__(self):
        modul = torch.nn.Sequential(torch.nn.Linear())